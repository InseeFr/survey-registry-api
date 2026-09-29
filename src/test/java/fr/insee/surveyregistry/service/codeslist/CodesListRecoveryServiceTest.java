package fr.insee.surveyregistry.service.codeslist;

import fr.insee.surveyregistry.dto.codeslist.CodesListContentDto;
import fr.insee.surveyregistry.dto.codeslist.CodesListMetadataDto;
import fr.insee.surveyregistry.dto.codeslist.CodesListSearchConfigDto;
import fr.insee.surveyregistry.entity.CodesListEntity;
import fr.insee.surveyregistry.enums.CodesListMetadataExpandableFieldsEnum;
import fr.insee.surveyregistry.mapper.codeslist.CodesListMetadataMapper;
import fr.insee.surveyregistry.repository.CodesListRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CodesListRecoveryServiceTest {

    private CodesListRepository repository;
    private CodesListMetadataMapper metadataMapper;
    private CodesListRecoveryService service;

    @BeforeEach
    void setUp() {
        repository = mock(CodesListRepository.class);
        metadataMapper = mock(CodesListMetadataMapper.class);
        service = new CodesListRecoveryService(repository, metadataMapper);
    }

    @Test
    void testGetAllMetadata() {
        UUID id = UUID.randomUUID();
        CodesListRepository.MetadataProjection projection = mock(CodesListRepository.MetadataProjection.class);
        when(projection.getId()).thenReturn(id);
        when(projection.getLabel()).thenReturn("Label1");
        when(projection.getVersion()).thenReturn(1);

        when(repository.findAllMetadata(null, null, null)).thenReturn(List.of(projection));

        CodesListMetadataDto dtoMock = new CodesListMetadataDto(id, "Label1", 1, "COMMUNES", "2024", "urn:ddi:communes:2024:1", false, true, null);
        when(metadataMapper.toDto(projection, null)).thenReturn(dtoMock);

        List<CodesListMetadataDto> result = service.getAllMetadata(null, null, null, null);

        assertEquals(1, result.size());
        CodesListMetadataDto dto = result.getFirst();
        assertEquals(id, dto.id());
        assertEquals("Label1", dto.label());
        assertEquals(1, dto.version());
        assertEquals("COMMUNES", dto.theme());
        assertEquals("2024", dto.referenceYear());
        assertEquals("urn:ddi:communes:2024:1", dto.urn());
        assertFalse(dto.isDeprecated());
        assertTrue(dto.isValid());

        verify(repository).findAllMetadata(null, null, null);
        verify(metadataMapper).toDto(projection, null);
    }

    @Test
    void testGetAllMetadataWithFilters() {
        when(repository.findAllMetadata(true, false, null)).thenReturn(List.of());

        service.getAllMetadata(null, true, false, null);

        verify(repository).findAllMetadata(true, false, null);
    }

    @Test
    void testGetMetadataById_Found() {
        UUID id = UUID.randomUUID();
        CodesListRepository.MetadataProjection projection = mock(CodesListRepository.MetadataProjection.class);

        CodesListSearchConfigDto searchConfig = new CodesListSearchConfigDto(Map.of("filter", true));

        when(projection.getId()).thenReturn(id);
        when(projection.getLabel()).thenReturn("Label2");
        when(projection.getVersion()).thenReturn(2);
        when(projection.getTheme()).thenReturn("COMMUNES");
        when(projection.getReferenceYear()).thenReturn("2024");
        when(projection.getUrn()).thenReturn("urn:ddi:communes:2024:1");
        when(projection.getSearchConfiguration()).thenReturn(searchConfig);

        CodesListMetadataDto mappedDto = new CodesListMetadataDto(id, "Label2", 2, "COMMUNES","2024", "urn:ddi:communes:2024:1", false, true, null);

        when(repository.findMetadataById(id)).thenReturn(Optional.of(projection));
        when(metadataMapper.toDto(projection, null)).thenReturn(mappedDto);

        Optional<CodesListMetadataDto> result = service.getMetadataById(id);

        assertTrue(result.isPresent());
        assertSame(mappedDto, result.get());
    }

    @Test
    void testGetMetadataById_withExpandSearchConfiguration() {
        UUID id = UUID.randomUUID();
        CodesListRepository.MetadataProjection projection = mock(CodesListRepository.MetadataProjection.class);

        CodesListSearchConfigDto searchConfig = new CodesListSearchConfigDto(Map.of("filter", true));

        when(projection.getId()).thenReturn(id);
        when(projection.getLabel()).thenReturn("Label2");
        when(projection.getVersion()).thenReturn(2);
        when(projection.getTheme()).thenReturn("COMMUNES");
        when(projection.getReferenceYear()).thenReturn("2024");
        when(projection.getUrn()).thenReturn("urn:ddi:communes:2024:1");
        when(projection.getSearchConfiguration()).thenReturn(searchConfig);

        CodesListMetadataDto mappedDto = new CodesListMetadataDto(id, "Label2", 2, "COMMUNES","2024", "urn:ddi:communes:2024:1", false, true, searchConfig);

        List<CodesListMetadataExpandableFieldsEnum> expand = List.of(CodesListMetadataExpandableFieldsEnum.SEARCH_CONFIGURATION);

        when(repository.findMetadataById(id)).thenReturn(Optional.of(projection));
        when(metadataMapper.toDto(projection, expand)).thenReturn(mappedDto);

        Optional<CodesListMetadataDto> result = service.getMetadataById(id, expand);

        assertTrue(result.isPresent());
        assertSame(mappedDto, result.get());
    }

    @Test
    void testGetAllMetadataFilteredByUrn() {
        String urn = "urn:ddi:communes:2024:1";

        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        CodesListRepository.MetadataProjection projection1 = mock(CodesListRepository.MetadataProjection.class);
        CodesListRepository.MetadataProjection projection2 = mock(CodesListRepository.MetadataProjection.class);

        when(repository.findAllMetadata(null, null, urn)).thenReturn(List.of(projection1, projection2));

        CodesListMetadataDto dto1 = new CodesListMetadataDto(id1, "Label1", 1, "COMMUNES", "2024", urn, true, true, null);
        CodesListMetadataDto dto2 = new CodesListMetadataDto(id2, "Label2", 2, "COMMUNES", "2024", urn, false, true, null);

        when(metadataMapper.toDto(projection1, null)).thenReturn(dto1);
        when(metadataMapper.toDto(projection2, null)).thenReturn(dto2);

        List<CodesListMetadataDto> result = service.getAllMetadata(null, null, null, urn);

        assertEquals(2, result.size());
        assertEquals(dto1, result.get(0));
        assertEquals(dto2, result.get(1));

        verify(repository).findAllMetadata(null, null, urn);
        verify(metadataMapper).toDto(projection1, null);
        verify(metadataMapper).toDto(projection2, null);
    }

    @Test
    void testGetAllMetadataFilteredByUrn_withExpandSearchConfiguration() {
        String urn = "urn:ddi:communes:2024:1";
        UUID id = UUID.randomUUID();

        CodesListRepository.MetadataProjection projection = mock(CodesListRepository.MetadataProjection.class);
        CodesListSearchConfigDto searchConfig = new CodesListSearchConfigDto(Map.of("filter", true));

        when(projection.getId()).thenReturn(id);
        when(projection.getLabel()).thenReturn("Label1");
        when(projection.getVersion()).thenReturn(1);
        when(projection.getTheme()).thenReturn("COMMUNES");
        when(projection.getReferenceYear()).thenReturn("2024");
        when(projection.getUrn()).thenReturn(urn);
        when(projection.getSearchConfiguration()).thenReturn(searchConfig);

        CodesListMetadataDto mappedDto = new CodesListMetadataDto(id, "Label1", 1, "COMMUNES", "2024", urn, false, true, searchConfig);

        List<CodesListMetadataExpandableFieldsEnum> expand = List.of(CodesListMetadataExpandableFieldsEnum.SEARCH_CONFIGURATION);

        when(repository.findAllMetadata(null, null, urn)).thenReturn(List.of(projection));
        when(metadataMapper.toDto(projection, expand)).thenReturn(mappedDto);

        List<CodesListMetadataDto> result = service.getAllMetadata(expand, null, null, urn);

        assertEquals(1, result.size());
        assertSame(mappedDto, result.getFirst());
        assertEquals(true, result.getFirst().searchConfiguration().content().get("filter"));
    }

    @Test
    void testGetAllMetadataFilteredByUrn_NotFound() {
        String urn = "urn:unknown";

        when(repository.findAllMetadata(null, null, urn)).thenReturn(List.of());

        List<CodesListMetadataDto> result = service.getAllMetadata(null, null, null, urn);

        assertTrue(result.isEmpty());

        verify(repository).findAllMetadata(null, null, urn);
        verify(metadataMapper, never()).toDto(any(), any());
    }

    @Test
    void testGetAllMetadataFilteredByUrnAndValid() {
        String urn = "urn:ddi:communes:2024:1";

        when(repository.findAllMetadata(true, null, urn)).thenReturn(List.of());

        service.getAllMetadata(null, true, null, urn);

        verify(repository).findAllMetadata(true, null, urn);
    }

    @Test
    void testGetCodesListById_Found() {
        List<Map<String, Object>> contentList = List.of(
                Map.of("id", "Code1", "label", "Label1")
        );

        CodesListEntity entity = new CodesListEntity();
        entity.setContent(new CodesListContentDto(contentList));

        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.of(entity));

        Optional<CodesListContentDto> result = service.getCodesListById(id);

        assertTrue(result.isPresent());
        CodesListContentDto contentWrapper = result.get();

        assertEquals("Code1", contentWrapper.items().getFirst().get("id"));
        assertEquals("Label1", contentWrapper.items().getFirst().get("label"));
    }

    @Test
    void testGetSearchConfiguration_Found() {
        Map<String,Object> searchConfigMap = Map.of("filter", true);

        CodesListEntity entity = new CodesListEntity();
        entity.setSearchConfiguration(new CodesListSearchConfigDto(searchConfigMap));

        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.of(entity));

        Optional<CodesListSearchConfigDto> result = service.getSearchConfiguration(id);

        assertTrue(result.isPresent());
        CodesListSearchConfigDto configWrapper = result.get();

        assertEquals(true, configWrapper.content().get("filter"));
    }

    @Test
    void testGetSearchConfiguration_NotFound() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        Optional<CodesListSearchConfigDto> result = service.getSearchConfiguration(id);

        assertTrue(result.isEmpty());
    }
}
