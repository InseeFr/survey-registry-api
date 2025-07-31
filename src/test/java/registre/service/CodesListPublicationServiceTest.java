package registre.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import registre.dto.CodesListDto;
import registre.dto.CodesListExternalLinkDto;
import registre.entity.CodesListEntity;
import registre.exception.InvalidSearchConfigurationException;
import registre.mapper.CodesListMapper;
import registre.repository.CodesListRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CodesListPublicationServiceTest {

    private CodesListRepository repository;
    private CodesListMapper codesListMapper;
    private CodesListPublicationService service;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        repository = mock(CodesListRepository.class);
        codesListMapper = mock(CodesListMapper.class);
        objectMapper = new ObjectMapper();

        service = new CodesListPublicationService(repository, codesListMapper);
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

        ArrayNode contentJson = objectMapper.createArrayNode();
        contentJson.add("code1");
        contentJson.add("code2");

        service.updateContent(id, contentJson);

        assertEquals(contentJson, entity.getContent());
        verify(repository).save(entity);
    }

    @Test
    void testUpdateContent_WhenCodesListDoesNotExist() {
        when(repository.findById("invalid-id")).thenReturn(Optional.empty());
        Executable executable = () -> service.updateContent("invalid-id", objectMapper.createArrayNode());
        assertThrows(IllegalArgumentException.class, executable);
    }

    @Test
    void testUpdateExternalLink_WhenCodesListExists() {
        String id = UUID.randomUUID().toString();
        CodesListEntity entity = new CodesListEntity();
        when(repository.findById(id)).thenReturn(Optional.of(entity));

        CodesListExternalLinkDto externalLinkDto = new CodesListExternalLinkDto();
        externalLinkDto.setVersion("v1");

        service.updateExternalLink(id, externalLinkDto);

        assertEquals("v1", entity.getExternalLinkVersion());
        verify(repository).save(entity);
    }

    @Test
    void testUpdateExternalLink_WhenCodesListDoesNotExist() {
        when(repository.findById("invalid-id")).thenReturn(Optional.empty());
        Executable executable = () -> service.updateExternalLink("invalid-id", new CodesListExternalLinkDto());
        assertThrows(IllegalArgumentException.class, executable);
    }

    @Test
    void testUpdateSearchConfiguration_WithValidJson() {
        String id = UUID.randomUUID().toString();
        CodesListEntity entity = new CodesListEntity();
        when(repository.findById(id)).thenReturn(Optional.of(entity));

        ObjectNode searchConfig = objectMapper.createObjectNode();
        searchConfig.put("type", "advanced");

        service.updateSearchConfiguration(id, searchConfig);

        assertNotNull(entity.getSearchConfiguration());
        assertEquals(searchConfig, entity.getSearchConfiguration());
        assertNotNull(entity.getSearchConfiguration().get("id"));
        verify(repository).save(entity);
    }

    @Test
    void testUpdateSearchConfiguration_AddsIdIfMissing() {
        String id = UUID.randomUUID().toString();
        CodesListEntity entity = new CodesListEntity();
        when(repository.findById(id)).thenReturn(Optional.of(entity));

        ObjectNode searchConfig = objectMapper.createObjectNode();
        service.updateSearchConfiguration(id, searchConfig);

        assertNotNull(entity.getSearchConfiguration().get("id"));
        verify(repository).save(entity);
    }

    @Test
    void testUpdateSearchConfiguration_WithInvalidJson_ThrowsException() {
        String id = UUID.randomUUID().toString();
        CodesListEntity entity = new CodesListEntity();
        when(repository.findById(id)).thenReturn(Optional.of(entity));

        ArrayNode invalidJson = objectMapper.createArrayNode();

        assertThrows(InvalidSearchConfigurationException.class,
                () -> service.updateSearchConfiguration(id, invalidJson));
    }

    @Test
    void testUpdateSearchConfiguration_WhenCodesListDoesNotExist() {
        when(repository.findById("invalid-id")).thenReturn(Optional.empty());
        Executable executable = () -> service.updateSearchConfiguration("invalid-id", objectMapper.createObjectNode());
        assertThrows(IllegalArgumentException.class, executable);
    }
}
