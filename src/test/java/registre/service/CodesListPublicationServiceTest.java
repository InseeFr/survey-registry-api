package registre.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import registre.dto.CodesListDto;
import registre.dto.CodesListExternalLinkDto;
import registre.entity.CodesListEntity;
import registre.exception.InvalidSearchConfigurationException;
import registre.mapper.CodesListMapper;
import registre.repository.CodesListRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CodesListPublicationServiceTest {

    private CodesListRepository repository;
    private CodesListMapper codesListMapper;
    private CodeMapper codeMapper;
    private CodesListExternalLinkMapper externalLinkMapper;
    private CodesListPublicationService service;

    @BeforeEach
    void setUp() {
        repository = mock(CodesListRepository.class);
        codesListMapper = mock(CodesListMapper.class);
        codeMapper = mock(CodeMapper.class);
        ObjectMapper objectMapper = new ObjectMapper();
        externalLinkMapper = mock(CodesListExternalLinkMapper.class);

        service = new CodesListPublicationService(repository, codesListMapper, codeMapper, objectMapper, externalLinkMapper);
    }

    @Test
    void testCreateCodesList() {
        CodesListDto dto = new CodesListDto();
        CodesListEntity entity = new CodesListEntity();

        when(codesListMapper.toEntity(dto)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);

        String id = service.createCodesList(dto);

        assertNotNull(id);
        verify(repository).save(entity);
    }

    @Test
    void testUpdateContent_WhenCodesListExists() {
        String id = UUID.randomUUID().toString();
        CodesListEntity entity = new CodesListEntity();
        when(repository.findById(id)).thenReturn(Optional.of(entity));

        CodeDto codeDto = new CodeDto();
        CodeEntity codeEntity = new CodeEntity();
        when(codeMapper.toEntity(codeDto)).thenReturn(codeEntity);

        service.updateContent(id, List.of(codeDto));

        assertEquals(1, entity.getContent().size());
        assertEquals(entity, codeEntity.getCodesList());
        verify(repository).save(entity);
    }

    @Test
    void testUpdateContent_WhenCodesListDoesNotExist() {
        when(repository.findById("invalid-id")).thenReturn(Optional.empty());
        Executable executable = () -> service.updateContent("invalid-id", List.of());
        assertThrows(IllegalArgumentException.class, executable);
    }

    @Test
    void testUpdateExternalLink() {
        String id = UUID.randomUUID().toString();
        CodesListEntity entity = new CodesListEntity();
        MetadataEntity metadata = new MetadataEntity();
        entity.setMetadata(metadata);
        when(repository.findById(id)).thenReturn(Optional.of(entity));

        CodesListExternalLinkDto dto = new CodesListExternalLinkDto();
        CodesListExternalLinkEntity externalEntity = new CodesListExternalLinkEntity();
        when(externalLinkMapper.toEntity(dto)).thenReturn(externalEntity);

        service.updateExternalLink(id, dto);

        assertEquals(externalEntity, metadata.getExternalLink());
        verify(repository).save(entity);
    }

    static class SearchConfig {
        public String type;

        public SearchConfig(String type) {
            this.type = type;
        }
    }
    @Test
    void testUpdateSearchConfiguration_WithValidJson() {
        String id = UUID.randomUUID().toString();
        CodesListEntity entity = new CodesListEntity();
        when(repository.findById(id)).thenReturn(Optional.of(entity));

        SearchConfig config = new SearchConfig("simple");

        service.updateSearchConfiguration(id, config);

        assertNotNull(entity.getSearchConfiguration());
        assertTrue(entity.getSearchConfiguration().getJsonContent().contains("simple"));
        verify(repository).save(entity);
    }

    @Test
    void testUpdateSearchConfiguration_WithInvalidJson() {
        String id = UUID.randomUUID().toString();
        CodesListEntity entity = new CodesListEntity();
        when(repository.findById(id)).thenReturn(Optional.of(entity));

        Object invalid = new Object();

        ObjectMapper failingObjectMapper = mock(ObjectMapper.class);
        service = new CodesListPublicationService(
                repository, codesListMapper, codeMapper, failingObjectMapper, externalLinkMapper
        );

        when(failingObjectMapper.valueToTree(any()))
                .thenThrow(new IllegalArgumentException("Mocked failure in valueToTree"));

        assertThrows(InvalidSearchConfigurationException.class, () ->
                service.updateSearchConfiguration(id, invalid));
    }

}
