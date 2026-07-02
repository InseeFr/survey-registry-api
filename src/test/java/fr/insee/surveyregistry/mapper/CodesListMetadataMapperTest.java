package fr.insee.surveyregistry.mapper;

import fr.insee.surveyregistry.dto.CodesListMetadataDto;
import fr.insee.surveyregistry.dto.SearchConfig;
import fr.insee.surveyregistry.enums.CodesListMetadataExpandableFieldsEnum;
import fr.insee.surveyregistry.repository.CodesListRepository.MetadataProjection;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CodesListMetadataMapperTest {

    private final CodesListMetadataMapper metadataMapper = new CodesListMetadataMapper();

    @Test
    void toDto_shouldReturnNull_whenProjectionIsNull() {
        CodesListMetadataDto dto = metadataMapper.toDto(null);
        assertNull(dto);
    }

    @Test
    void toDto_shouldMapFields() {
        MetadataProjection projection = mock(MetadataProjection.class);
        UUID id = UUID.randomUUID();

        when(projection.getId()).thenReturn(id);
        when(projection.getLabel()).thenReturn("Label1");
        when(projection.getVersion()).thenReturn(1);
        when(projection.getTheme()).thenReturn("COMMUNES");
        when(projection.getReferenceYear()).thenReturn("2024");
        when(projection.getUrn()).thenReturn("urn:ddi:communes:2024:1");
        when(projection.isDeprecated()).thenReturn(false);
        when(projection.isValid()).thenReturn(true);

        CodesListMetadataDto dto = metadataMapper.toDto(projection);

        assertNotNull(dto);
        assertEquals(id, dto.id());
        assertEquals("Label1", dto.label());
        assertEquals(1, dto.version().intValue());
        assertEquals("COMMUNES", dto.theme());
        assertEquals("2024", dto.referenceYear());
        assertEquals("urn:ddi:communes:2024:1", dto.urn());
        assertFalse(dto.isDeprecated());
        assertTrue(dto.isValid());
    }

    @Test
    void toDto_shouldIncludeSearchConfiguration_whenExpandHasSearchConfiguration() {
        MetadataProjection projection = mock(MetadataProjection.class);
        UUID id = UUID.randomUUID();

        when(projection.getId()).thenReturn(id);
        when(projection.getLabel()).thenReturn("Label1");
        when(projection.getVersion()).thenReturn(1);
        when(projection.getTheme()).thenReturn("COMMUNES");
        when(projection.getReferenceYear()).thenReturn("2024");
        when(projection.getUrn()).thenReturn("urn:ddi:communes:2024:1");
        when(projection.isDeprecated()).thenReturn(false);
        when(projection.isValid()).thenReturn(true);
        when(projection.getSearchConfiguration()).thenReturn(new SearchConfig(Map.of("enabled", true)));

        List<CodesListMetadataExpandableFieldsEnum> expand = List.of(CodesListMetadataExpandableFieldsEnum.SEARCH_CONFIGURATION);
        CodesListMetadataDto dto = metadataMapper.toDto(projection, expand);

        assertNotNull(dto);
        assertEquals(new SearchConfig(Map.of("enabled", true)), dto.searchConfiguration());
    }

    @Test
    void toDto_shouldNotIncludeSearchConfiguration_whenExpandDoesNotHaveSearchConfiguration() {
        MetadataProjection projection = mock(MetadataProjection.class);
        UUID id = UUID.randomUUID();

        when(projection.getId()).thenReturn(id);
        when(projection.getLabel()).thenReturn("Label1");
        when(projection.getVersion()).thenReturn(1);
        when(projection.getTheme()).thenReturn("COMMUNES");
        when(projection.getReferenceYear()).thenReturn("2024");
        when(projection.getUrn()).thenReturn("urn:ddi:communes:2024:1");
        when(projection.isDeprecated()).thenReturn(false);
        when(projection.isValid()).thenReturn(true);
        when(projection.getSearchConfiguration()).thenReturn(new SearchConfig(Map.of("enabled", true)));

        List<CodesListMetadataExpandableFieldsEnum> expand = List.of();
        CodesListMetadataDto dto = metadataMapper.toDto(projection, expand);

        assertNotNull(dto);
        assertNull(dto.searchConfiguration());
    }
}