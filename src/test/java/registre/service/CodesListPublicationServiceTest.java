package registre.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import registre.dto.CodesListDto;
import registre.dto.CodesListExternalLinkDto;
import registre.entity.CodesListEntity;
import registre.entity.CodesListExternalLinkEntity;
import registre.mapper.CodesListMapper;
import registre.repository.CodesListExternalLinkRepository;
import registre.repository.CodesListRepository;

import java.util.Objects;
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
        CodesListDto dto = new CodesListDto(null,null,null,null);
        CodesListEntity entity = new CodesListEntity();

        when(codesListMapper.toEntity(dto)).thenReturn(entity);
        when(codesListRepository.save(entity)).thenReturn(entity);

        UUID id = service.createCodesList(dto);

        assertNotNull(id);
        verify(codesListRepository).save(entity);
    }

    @Test
    void testCreateContent_WhenCodesListExists() {
        UUID id = UUID.randomUUID();
        CodesListEntity entity = new CodesListEntity();

        when(codesListRepository.existsById(id)).thenReturn(true);
        when(codesListRepository.existsContent(id)).thenReturn(false);
        when(codesListRepository.findById(id)).thenReturn(Optional.of(entity));

        ArrayNode contentJson = objectMapper.createArrayNode();
        contentJson.add("code1");
        contentJson.add("code2");

        service.createContent(id, contentJson);

        assertEquals(contentJson, entity.getContent());
        verify(codesListRepository).save(entity);
    }

    @Test
    void testCreateContent_WhenCodesListDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(codesListRepository.findById(id)).thenReturn(Optional.empty());
        Executable executable = () -> service.createContent(id, objectMapper.createArrayNode());
        assertThrows(IllegalArgumentException.class, executable);
    }

    @Test
    void testCreateContent_WhenContentAlreadyExists() {
        UUID id = UUID.randomUUID();

        when(codesListRepository.existsById(id)).thenReturn(true);
        when(codesListRepository.existsContent(id)).thenReturn(true);

        ArrayNode contentJson = objectMapper.createArrayNode();
        contentJson.add("code1");

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.createContent(id, contentJson));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        assertTrue(Objects.requireNonNull(ex.getReason()).contains("already exists"));
        verify(codesListRepository, never()).save(any());
    }

    @Test
    void testCreateExternalLink_WhenCodesListExists() {
        UUID id = UUID.randomUUID();
        CodesListEntity entity = new CodesListEntity();

        when(codesListRepository.existsById(id)).thenReturn(true);
        when(codesListRepository.existsExternalLink(id)).thenReturn(false);
        when(codesListRepository.findById(id)).thenReturn(Optional.of(entity));

        CodesListExternalLinkDto externalLinkDto = new CodesListExternalLinkDto("ExternalLink1", "v1");

        CodesListExternalLinkEntity externalLinkEntity = new CodesListExternalLinkEntity();
        externalLinkEntity.setVersion("v1");
        when(externalLinkRepository.findById("ExternalLink1")).thenReturn(Optional.of(externalLinkEntity));

        service.createExternalLink(id, externalLinkDto);

        assertEquals("v1", entity.getCodesListExternalLink().getVersion());
        verify(codesListRepository).save(entity);
    }


    @Test
    void testCreateExternalLink_WhenCodesListDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(codesListRepository.findById(id)).thenReturn(Optional.empty());
        Executable executable = () -> service.createExternalLink(id, new CodesListExternalLinkDto("ExternalLink1", "v1"));
        assertThrows(IllegalArgumentException.class, executable);
    }

    @Test
    void testCreateExternalLink_WhenExternalLinkAlreadyExists() {
        UUID id = UUID.randomUUID();

        when(codesListRepository.existsById(id)).thenReturn(true);
        when(codesListRepository.existsExternalLink(id)).thenReturn(true);

        CodesListExternalLinkDto dto = new CodesListExternalLinkDto("ExternalLink1", "v1");

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.createExternalLink(id, dto));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        assertTrue(Objects.requireNonNull(ex.getReason()).contains("already exists"));
        verify(codesListRepository, never()).save(any());
    }

    @Test
    void testCreateSearchConfiguration_WithValidJson() {
        UUID id = UUID.randomUUID();
        CodesListEntity entity = new CodesListEntity();

        when(codesListRepository.existsById(id)).thenReturn(true);
        when(codesListRepository.existsSearchConfiguration(id)).thenReturn(false);
        when(codesListRepository.findById(id)).thenReturn(Optional.of(entity));

        ObjectNode searchConfig = objectMapper.createObjectNode();
        searchConfig.put("type", "advanced");

        service.createSearchConfiguration(id, searchConfig);

        assertNotNull(entity.getSearchConfiguration());
        assertEquals(searchConfig, entity.getSearchConfiguration());
        verify(codesListRepository).save(entity);
    }

    @Test
    void testCreateSearchConfiguration_AddsIdIfMissing() {
        UUID id = UUID.randomUUID();
        CodesListEntity entity = new CodesListEntity();

        when(codesListRepository.existsById(id)).thenReturn(true);
        when(codesListRepository.existsSearchConfiguration(id)).thenReturn(false);
        when(codesListRepository.findById(id)).thenReturn(Optional.of(entity));

        ObjectNode searchConfig = objectMapper.createObjectNode();
        service.createSearchConfiguration(id, searchConfig);

        verify(codesListRepository).save(entity);
    }

    @Test
    void testCreateSearchConfiguration_WhenCodesListDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(codesListRepository.findById(id)).thenReturn(Optional.empty());
        Executable executable = () -> service.createSearchConfiguration(id, objectMapper.createObjectNode());
        assertThrows(IllegalArgumentException.class, executable);
    }

    @Test
    void testCreateSearchConfiguration_WhenConfigAlreadyExists() {
        UUID id = UUID.randomUUID();

        when(codesListRepository.existsById(id)).thenReturn(true);
        when(codesListRepository.existsSearchConfiguration(id)).thenReturn(true);

        ObjectNode configJson = objectMapper.createObjectNode();
        configJson.put("type", "advanced");

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.createSearchConfiguration(id, configJson));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        assertTrue(Objects.requireNonNull(ex.getReason()).contains("already exists"));
        verify(codesListRepository, never()).save(any());
    }
}
