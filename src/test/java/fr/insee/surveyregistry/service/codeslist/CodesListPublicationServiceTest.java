package fr.insee.surveyregistry.service.codeslist;

import fr.insee.surveyregistry.dto.codeslist.CodesListContentDto;
import fr.insee.surveyregistry.dto.codeslist.CodesListDto;
import fr.insee.surveyregistry.dto.codeslist.CodesListMetadataDto;
import fr.insee.surveyregistry.dto.codeslist.CodesListSearchConfigDto;
import fr.insee.surveyregistry.entity.CodesListEntity;
import fr.insee.surveyregistry.exception.ResourceAlreadyExistsException;
import fr.insee.surveyregistry.exception.ResourceNotFoundException;
import fr.insee.surveyregistry.exception.ResourceStateConflictException;
import fr.insee.surveyregistry.mapper.codeslist.CodesListMapper;
import fr.insee.surveyregistry.repository.CodesListRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CodesListPublicationServiceTest {

    private CodesListRepository codesListRepository;
    private CodesListMapper codesListMapper;
    private CodesListPublicationService service;

    @BeforeEach
    void setUp() {
        codesListRepository = mock(CodesListRepository.class);
        codesListMapper = mock(CodesListMapper.class);

        service = new CodesListPublicationService(codesListRepository, codesListMapper);
    }

    @Test
    void testCreateCodesListMetadataOnly() {
        CodesListMetadataDto metadataDto = new CodesListMetadataDto(
                null,
                "Label1",
                1,
                "COMMUNES",
                "2024",
                "urn:ddi:communes:2024:1",
                false,
                true,
                null
        );

        CodesListEntity entity = new CodesListEntity();
        when(codesListMapper.toEntity(any(CodesListDto.class))).thenReturn(entity);
        when(codesListRepository.save(entity)).thenReturn(entity);

        service.createCodesListMetadataOnly(metadataDto);

        verify(codesListRepository, times(1)).save(entity);
    }

    @Test
    void testCreateCodesList() {
        CodesListDto dto = new CodesListDto(null, null, null, null);
        CodesListEntity entity = new CodesListEntity();

        when(codesListMapper.toEntity(dto)).thenReturn(entity);
        when(codesListRepository.save(entity)).thenReturn(entity);

        UUID id = service.createCodesList(dto);

        assertNotNull(id);
        verify(codesListRepository).save(entity);
    }

    @Test
    void testCreateCodesList_WhenAlreadyExists() {
        CodesListDto dto = new CodesListDto(null, null, null, null);
        CodesListEntity entity = new CodesListEntity();

        when(codesListMapper.toEntity(dto)).thenReturn(entity);
        when(codesListRepository.existsByThemeAndReferenceYearAndVersion(any(), any(), any()))
                .thenReturn(true);

        ResourceAlreadyExistsException ex = assertThrows(ResourceAlreadyExistsException.class,
                () -> service.createCodesList(dto));

        assertTrue(ex.getMessage().contains("already exists"));
        verify(codesListRepository, never()).save(any());
    }

    @Test
    void testDeprecateOlderVersions_CallsRepositoryCorrectly() {
        String theme = "COMMUNES";
        String referenceYear = "2025";
        UUID currentId = UUID.randomUUID();

        service.deprecateOlderVersions(theme, referenceYear, currentId);

        verify(codesListRepository, times(1))
                .deprecateOlderVersions(theme, referenceYear, currentId);
    }

    @Test
    void testCreateContent_WhenCodesListExists() {
        UUID id = UUID.randomUUID();
        CodesListEntity entity = new CodesListEntity();

        when(codesListRepository.findById(id)).thenReturn(Optional.of(entity));
        when(codesListRepository.existsByIdAndContentIsNotNull(id)).thenReturn(false);

        List<Map<String, Object>> contentList = List.of(
                Map.of("code", "code1"),
                Map.of("code", "code2")
        );

        service.createContent(id, new CodesListContentDto(contentList));

        CodesListContentDto createdContent = entity.getContent();

        assertNotNull(createdContent);
        assertEquals(2, createdContent.items().size());
        assertEquals("code1", createdContent.items().get(0).get("code"));
        assertEquals("code2", createdContent.items().get(1).get("code"));

        verify(codesListRepository).save(entity);
    }

    @Test
    void testCreateContent_WhenCodesListDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(codesListRepository.findById(id)).thenReturn(Optional.empty());

        List<Map<String, Object>> contentList = List.of(Map.of("code", "dummy"));
        CodesListContentDto contentWrapper = new CodesListContentDto(contentList);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> service.createContent(id, contentWrapper));

        assertTrue(ex.getMessage().contains(id.toString()));
        verify(codesListRepository, never()).save(any());
    }

    @Test
    void testCreateContent_WhenContentAlreadyExists() {
        UUID id = UUID.randomUUID();

        when(codesListRepository.findById(id)).thenReturn(Optional.of(new CodesListEntity()));
        when(codesListRepository.existsByIdAndContentIsNotNull(id)).thenReturn(true);

        List<Map<String, Object>> contentList = List.of(Map.of("code", "code1"));
        CodesListContentDto contentWrapper = new CodesListContentDto(contentList);

        ResourceAlreadyExistsException ex = assertThrows(ResourceAlreadyExistsException.class,
                () -> service.createContent(id, contentWrapper));

        assertTrue(ex.getMessage().contains("already exists"));
        verify(codesListRepository, never()).save(any());
    }

    @Test
    void testCreateSearchConfiguration_WithValidJson() {
        UUID id = UUID.randomUUID();
        CodesListEntity entity = new CodesListEntity();

        when(codesListRepository.findById(id)).thenReturn(Optional.of(entity));
        when(codesListRepository.existsByIdAndSearchConfigurationIsNotNull(id)).thenReturn(false);

        Map<String, Object> searchConfigMap = Map.of("type", "advanced");
        CodesListSearchConfigDto searchConfig = new CodesListSearchConfigDto(searchConfigMap);

        service.createSearchConfiguration(id, searchConfig);

        assertNotNull(entity.getSearchConfiguration());
        assertEquals(searchConfigMap, entity.getSearchConfiguration().content());
        verify(codesListRepository).save(entity);
    }

    @Test
    void testCreateSearchConfiguration_AddsIdIfMissing() {
        UUID id = UUID.randomUUID();
        CodesListEntity entity = new CodesListEntity();

        when(codesListRepository.findById(id)).thenReturn(Optional.of(entity));
        when(codesListRepository.existsByIdAndSearchConfigurationIsNotNull(id)).thenReturn(false);

        Map<String, Object> searchConfigMap = Map.of();
        CodesListSearchConfigDto searchConfig = new CodesListSearchConfigDto(searchConfigMap);

        service.createSearchConfiguration(id, searchConfig);

        verify(codesListRepository).save(entity);
    }

    @Test
    void testCreateSearchConfiguration_WhenCodesListDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(codesListRepository.findById(id)).thenReturn(Optional.empty());

        Map<String, Object> searchConfigMap = Map.of("type", "advanced");
        CodesListSearchConfigDto searchConfig = new CodesListSearchConfigDto(searchConfigMap);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> service.createSearchConfiguration(id, searchConfig));

        assertTrue(ex.getMessage().contains(id.toString()));
        verify(codesListRepository, never()).save(any());
    }

    @Test
    void testCreateSearchConfiguration_WhenConfigAlreadyExists() {
        UUID id = UUID.randomUUID();

        when(codesListRepository.findById(id)).thenReturn(Optional.of(new CodesListEntity()));
        when(codesListRepository.existsByIdAndSearchConfigurationIsNotNull(id)).thenReturn(true);

        Map<String, Object> configMap = Map.of("type", "advanced");
        CodesListSearchConfigDto configWrapper = new CodesListSearchConfigDto(configMap);

        ResourceAlreadyExistsException ex = assertThrows(ResourceAlreadyExistsException.class,
                () -> service.createSearchConfiguration(id, configWrapper));

        assertTrue(ex.getMessage().contains("already exists"));
        verify(codesListRepository, never()).save(any());
    }

    @Test
    void testCreateURN_WhenCodesListExists() {
        UUID id = UUID.randomUUID();
        CodesListEntity entity = new CodesListEntity();

        when(codesListRepository.findById(id)).thenReturn(Optional.of(entity));
        when(codesListRepository.existsByIdAndUrnIsNotNull(id)).thenReturn(false);

        String urn = "urn:ddi:communes:2024:1";

        service.createURN(id, urn);

        assertEquals(urn, entity.getUrn());
        verify(codesListRepository).save(entity);
    }

    @Test
    void testCreateURN_WhenCodesListDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(codesListRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> service.createURN(id, "urn:ddi:communes:2024:1"));

        assertTrue(ex.getMessage().contains(id.toString()));
        verify(codesListRepository, never()).save(any());
    }

    @Test
    void testCreateURN_WhenURNAlreadyExists() {
        UUID id = UUID.randomUUID();

        when(codesListRepository.findById(id)).thenReturn(Optional.of(new CodesListEntity()));
        when(codesListRepository.existsByIdAndUrnIsNotNull(id)).thenReturn(true);

        ResourceAlreadyExistsException ex = assertThrows(ResourceAlreadyExistsException.class,
                () -> service.createURN(id, "urn:ddi:communes:2024:1"));

        assertTrue(ex.getMessage().contains("already exists"));
        verify(codesListRepository, never()).save(any());
    }

    @Test
    void testMarkAsDeprecated() {
        UUID id = UUID.randomUUID();
        CodesListMetadataDto metadataDto = new CodesListMetadataDto(null, "LabelX", 1, "COMMUNES", "2025", "urn:ddi:communes:2024:1", false, true, null);
        CodesListDto dto = new CodesListDto(null, metadataDto, null, null);

        CodesListEntity entity = new CodesListEntity();
        entity.setId(id);

        when(codesListMapper.toEntity(dto)).thenReturn(entity);
        when(codesListRepository.save(entity)).thenReturn(entity);
        when(codesListRepository.findById(id)).thenReturn(Optional.of(entity));

        service.createCodesList(dto);
        assertFalse(entity.isDeprecated());

        service.markAsDeprecated(id);
        assertTrue(entity.isDeprecated());

        ResourceStateConflictException ex = assertThrows(ResourceStateConflictException.class,
                () -> service.markAsDeprecated(id));
        assertTrue(ex.getMessage().contains("already deprecated"));
    }

    @Test
    void testMarkAsDeprecated_WhenCodesListDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(codesListRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.markAsDeprecated(id));
        verify(codesListRepository, never()).save(any());
    }

    @Test
    void testMarkAsInvalid() {
        UUID id = UUID.randomUUID();
        CodesListMetadataDto metadataDto = new CodesListMetadataDto(null, "LabelX", 1, "COMMUNES", "2025", "urn:ddi:communes:2024:1", false, true, null);
        CodesListDto dto = new CodesListDto(null, metadataDto, null, null);

        CodesListEntity entity = new CodesListEntity();
        entity.setId(id);

        when(codesListMapper.toEntity(dto)).thenReturn(entity);
        when(codesListRepository.save(entity)).thenReturn(entity);
        when(codesListRepository.findById(id)).thenReturn(Optional.of(entity));

        service.createCodesList(dto);
        assertTrue(entity.isValid());

        service.markAsInvalid(id);
        assertFalse(entity.isValid());

        ResourceStateConflictException ex = assertThrows(ResourceStateConflictException.class,
                () -> service.markAsInvalid(id));
        assertTrue(ex.getMessage().contains("already marked as invalid"));
    }

    @Test
    void testMarkAsInvalid_WhenCodesListDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(codesListRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.markAsInvalid(id));
        verify(codesListRepository, never()).save(any());
    }
}
