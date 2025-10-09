package registre.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import registre.dto.*;
import registre.entity.CodesListEntity;
import registre.entity.CodesListExternalLinkEntity;
import registre.mapper.CodesListMapper;
import registre.repository.CodesListExternalLinkRepository;
import registre.repository.CodesListRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CodesListPublicationServiceTest {

    private CodesListExternalLinkRepository externalLinkRepository;
    private CodesListRepository codesListRepository;
    private CodesListMapper codesListMapper;
    private CodesListPublicationService service;

    @BeforeEach
    void setUp() {
        codesListRepository = mock(CodesListRepository.class);
        externalLinkRepository = mock(CodesListExternalLinkRepository.class);
        codesListMapper = mock(CodesListMapper.class);

        service = new CodesListPublicationService(externalLinkRepository, codesListRepository, codesListMapper);
    }

    @Test
    void testCreateCodesListMetadataOnly_WithExternalLink() {
        MetadataDto metadataDto = new MetadataDto(
                null,
                "Label1",
                1,
                "COMMUNES",
                "2024",
                new CodesListExternalLinkDto("ExternalLink1"),
                false
        );

        CodesListEntity entity = new CodesListEntity();
        when(codesListMapper.toEntity(any(CodesListDto.class))).thenReturn(entity);
        when(codesListRepository.save(entity)).thenReturn(entity);
        when(codesListRepository.existsById(any(UUID.class))).thenReturn(true);
        when(codesListRepository.findById(any(UUID.class))).thenReturn(Optional.of(entity));

        CodesListExternalLinkEntity externalLinkEntity = new CodesListExternalLinkEntity();
        externalLinkEntity.setId("ExternalLink1");
        externalLinkEntity.setVersion("v1");
        when(externalLinkRepository.findById("ExternalLink1")).thenReturn(Optional.of(externalLinkEntity));

        service.createCodesListMetadataOnly(metadataDto);

        verify(codesListRepository, times(2)).save(entity);
    }

    @Test
    void testCreateCodesListMetadataOnly_WithoutExternalLink() {
        MetadataDto metadataDto = new MetadataDto(null, "Label1", 1, "COMMUNES", "2024", null, false);

        CodesListEntity entity = new CodesListEntity();
        when(codesListMapper.toEntity(any(CodesListDto.class))).thenReturn(entity);
        when(codesListRepository.save(entity)).thenReturn(entity);

        service.createCodesListMetadataOnly(metadataDto);

        verify(codesListRepository, times(1)).save(entity);
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
        when(codesListRepository.existsByIdAndContentIsNotNull(id)).thenReturn(false);
        when(codesListRepository.findById(id)).thenReturn(Optional.of(entity));

        List<Map<String, Object>> contentList = List.of(
                Map.of("code", "code1"),
                Map.of("code", "code2")
        );

        service.createContent(id, new CodesListContent(contentList));

        CodesListContent createdContent = entity.getContent();

        assertNotNull(createdContent);
        assertEquals(2, createdContent.items().size());
        assertEquals("code1", createdContent.items().get(0).get("code"));
        assertEquals("code2", createdContent.items().get(1).get("code"));

        verify(codesListRepository).save(entity);
    }

    @Test
    void testCreateContent_WhenCodesListDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(codesListRepository.existsById(id)).thenReturn(false);

        List<Map<String, Object>> contentList = List.of(Map.of("code", "dummy"));

        Executable executable = () -> service.createContent(id, new CodesListContent(contentList));

        assertThrows(IllegalArgumentException.class, executable);
    }

    @Test
    void testCreateContent_WhenContentAlreadyExists() {
        UUID id = UUID.randomUUID();

        when(codesListRepository.existsById(id)).thenReturn(true);
        when(codesListRepository.existsByIdAndContentIsNotNull(id)).thenReturn(true);

        List<Map<String, Object>> contentList = List.of(Map.of("code", "code1"));
        CodesListContent contentWrapper = new CodesListContent(contentList);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.createContent(id, contentWrapper));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        assertTrue(Objects.requireNonNull(ex.getReason()).contains("already exists"));
        verify(codesListRepository, never()).save(any());
    }

    @Test
    void testCreateExternalLink_WhenCodesListExists() {
        UUID id = UUID.randomUUID();
        CodesListEntity entity = new CodesListEntity();

        when(codesListRepository.existsById(id)).thenReturn(true);
        when(codesListRepository.existsByIdAndCodesListExternalLinkIsNotNull(id)).thenReturn(false);
        when(codesListRepository.findById(id)).thenReturn(Optional.of(entity));

        CodesListExternalLinkDto externalLinkDto = new CodesListExternalLinkDto("ExternalLink1");

        CodesListExternalLinkEntity externalLinkEntity = new CodesListExternalLinkEntity();
        externalLinkEntity.setVersion("v1");
        when(externalLinkRepository.findById("ExternalLink1")).thenReturn(Optional.of(externalLinkEntity));

        service.createExternalLink(id, externalLinkDto);

        verify(codesListRepository).save(entity);
    }


    @Test
    void testCreateExternalLink_WhenCodesListDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(codesListRepository.findById(id)).thenReturn(Optional.empty());
        Executable executable = () -> service.createExternalLink(id, new CodesListExternalLinkDto("ExternalLink1"));
        assertThrows(IllegalArgumentException.class, executable);
    }

    @Test
    void testCreateExternalLink_WhenExternalLinkAlreadyExists() {
        UUID id = UUID.randomUUID();

        when(codesListRepository.existsById(id)).thenReturn(true);
        when(codesListRepository.existsByIdAndCodesListExternalLinkIsNotNull(id)).thenReturn(true);

        CodesListExternalLinkDto dto = new CodesListExternalLinkDto("ExternalLink1");

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
        when(codesListRepository.existsByIdAndSearchConfigurationIsNotNull(id)).thenReturn(false);
        when(codesListRepository.findById(id)).thenReturn(Optional.of(entity));

        Map<String,Object> searchConfigMap = Map.of("type", "advanced");
        SearchConfig searchConfig = new SearchConfig(searchConfigMap);

        service.createSearchConfiguration(id, searchConfig);

        assertNotNull(entity.getSearchConfiguration());
        assertEquals(searchConfigMap, entity.getSearchConfiguration().content());
        verify(codesListRepository).save(entity);
    }

    @Test
    void testCreateSearchConfiguration_AddsIdIfMissing() {
        UUID id = UUID.randomUUID();
        CodesListEntity entity = new CodesListEntity();

        when(codesListRepository.existsById(id)).thenReturn(true);
        when(codesListRepository.existsByIdAndSearchConfigurationIsNotNull(id)).thenReturn(false);
        when(codesListRepository.findById(id)).thenReturn(Optional.of(entity));

        Map<String,Object> searchConfigMap = Map.of();
        SearchConfig searchConfig = new SearchConfig(searchConfigMap);

        service.createSearchConfiguration(id, searchConfig);

        verify(codesListRepository).save(entity);
    }

    @Test
    void testCreateSearchConfiguration_WhenCodesListDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(codesListRepository.existsById(id)).thenReturn(false);

        Map<String,Object> searchConfigMap = Map.of("type", "advanced");
        SearchConfig searchConfig = new SearchConfig(searchConfigMap);

        Executable executable = () -> service.createSearchConfiguration(id, searchConfig);

        assertThrows(IllegalArgumentException.class, executable);
    }

    @Test
    void testCreateSearchConfiguration_WhenConfigAlreadyExists() {
        UUID id = UUID.randomUUID();

        when(codesListRepository.existsById(id)).thenReturn(true);
        when(codesListRepository.existsByIdAndSearchConfigurationIsNotNull(id)).thenReturn(true);

        Map<String,Object> configMap = Map.of("type", "advanced");
        SearchConfig configWrapper = new SearchConfig(configMap);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.createSearchConfiguration(id, configWrapper));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        assertTrue(Objects.requireNonNull(ex.getReason()).contains("already exists"));
        verify(codesListRepository, never()).save(any());
    }

    @Test
    void testMarkAsDeprecated() {
        UUID id = UUID.randomUUID();
        MetadataDto metadataDto = new MetadataDto(null, "LabelX", 1, "COMMUNES", "2025", null, false);
        CodesListDto dto = new CodesListDto(null, metadataDto, null, null);

        CodesListEntity entity = new CodesListEntity();
        entity.setId(id);

        when(codesListMapper.toEntity(dto)).thenReturn(entity);
        when(codesListRepository.save(entity)).thenReturn(entity);
        when(codesListRepository.existsById(id)).thenReturn(true);
        when(codesListRepository.findById(id)).thenReturn(Optional.of(entity));

        service.createCodesList(dto);
        assertFalse(entity.isDeprecated());

        service.markAsDeprecated(id);
        assertTrue(entity.isDeprecated());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.markAsDeprecated(id));
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    }
}
