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
import registre.entity.CodesListExternalLinkEntity;
import registre.mapper.CodesListMapper;
import registre.repository.CodesListExternalLinkRepository;
import registre.repository.CodesListRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CodesListPublicationServiceTest {

    private CodesListExternalLinkRepository externalLinkRepository;
    private CodesListRepository codesListRepository;
    private CodesListMapper codesListMapper;
    private CodesListPublicationService service;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        codesListRepository = mock(CodesListRepository.class);
        externalLinkRepository = mock(CodesListExternalLinkRepository.class);
        codesListMapper = mock(CodesListMapper.class);
        objectMapper = new ObjectMapper();

        service = new CodesListPublicationService(externalLinkRepository, codesListRepository, codesListMapper);
    }

    @Test
    void testCreateCodesList() {
        CodesListDto dto = new CodesListDto();
        CodesListEntity entity = new CodesListEntity();

        when(codesListMapper.toEntity(dto)).thenReturn(entity);
        when(codesListRepository.save(entity)).thenReturn(entity);

        String id = service.createCodesList(dto);

        assertNotNull(id);
        verify(codesListRepository).save(entity);
    }

    @Test
    void testUpdateContent_WhenCodesListExists() {
        String id = UUID.randomUUID().toString();
        CodesListEntity entity = new CodesListEntity();
        when(codesListRepository.findById(id)).thenReturn(Optional.of(entity));

        ArrayNode contentJson = objectMapper.createArrayNode();
        contentJson.add("code1");
        contentJson.add("code2");

        service.updateContent(id, contentJson);

        assertEquals(contentJson, entity.getContent());
        verify(codesListRepository).save(entity);
    }

    @Test
    void testUpdateContent_WhenCodesListDoesNotExist() {
        when(codesListRepository.findById("invalid-id")).thenReturn(Optional.empty());
        Executable executable = () -> service.updateContent("invalid-id", objectMapper.createArrayNode());
        assertThrows(IllegalArgumentException.class, executable);
    }

    @Test
    void testUpdateExternalLink_WhenCodesListExists() {
        String id = UUID.randomUUID().toString();
        CodesListEntity entity = new CodesListEntity();
        when(codesListRepository.findById(id)).thenReturn(Optional.of(entity));

        CodesListExternalLinkDto externalLinkDto = new CodesListExternalLinkDto();
        externalLinkDto.setId("ExternalLink1");
        externalLinkDto.setVersion("v1");

        CodesListExternalLinkEntity externalLinkEntity = new CodesListExternalLinkEntity();
        externalLinkEntity.setVersion("v1");
        when(externalLinkRepository.findById("ExternalLink1")).thenReturn(Optional.of(externalLinkEntity));

        service.updateExternalLink(id, externalLinkDto);

        assertEquals("v1", entity.getCodesListExternalLink().getVersion());
        verify(codesListRepository).save(entity);
    }


    @Test
    void testUpdateExternalLink_WhenCodesListDoesNotExist() {
        when(codesListRepository.findById("invalid-id")).thenReturn(Optional.empty());
        Executable executable = () -> service.updateExternalLink("invalid-id", new CodesListExternalLinkDto());
        assertThrows(IllegalArgumentException.class, executable);
    }

    @Test
    void testUpdateSearchConfiguration_WithValidJson() {
        String id = UUID.randomUUID().toString();
        CodesListEntity entity = new CodesListEntity();
        when(codesListRepository.findById(id)).thenReturn(Optional.of(entity));

        ObjectNode searchConfig = objectMapper.createObjectNode();
        searchConfig.put("type", "advanced");

        service.updateSearchConfiguration(id, searchConfig);

        assertNotNull(entity.getSearchConfiguration());
        assertEquals(searchConfig, entity.getSearchConfiguration());
        verify(codesListRepository).save(entity);
    }

    @Test
    void testUpdateSearchConfiguration_AddsIdIfMissing() {
        String id = UUID.randomUUID().toString();
        CodesListEntity entity = new CodesListEntity();
        when(codesListRepository.findById(id)).thenReturn(Optional.of(entity));

        ObjectNode searchConfig = objectMapper.createObjectNode();
        service.updateSearchConfiguration(id, searchConfig);

        verify(codesListRepository).save(entity);
    }

    @Test
    void testUpdateSearchConfiguration_WhenCodesListDoesNotExist() {
        when(codesListRepository.findById("invalid-id")).thenReturn(Optional.empty());
        Executable executable = () -> service.updateSearchConfiguration("invalid-id", objectMapper.createObjectNode());
        assertThrows(IllegalArgumentException.class, executable);
    }
}
