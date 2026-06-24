package fr.insee.surveyregistry.mapper;

import fr.insee.surveyregistry.dto.SearchConfig;
import fr.insee.surveyregistry.enums.CodesListMetadataExpandableFieldsEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import fr.insee.surveyregistry.dto.CodesListExternalLinkDto;
import fr.insee.surveyregistry.dto.CodesListMetadataDto;
import fr.insee.surveyregistry.entity.CodesListExternalLinkEntity;
import fr.insee.surveyregistry.repository.CodesListRepository.MetadataProjection;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CodesListMetadataMapperTest {

    private CodesListExternalLinkMapper externalLinkMapper;
    private CodesListMetadataMapper metadataMapper;

    @BeforeEach
    void setup() {
        externalLinkMapper = mock(CodesListExternalLinkMapper.class);
        metadataMapper = new CodesListMetadataMapper(externalLinkMapper);
    }

    @Test
    void toDto_shouldReturnNull_whenProjectionIsNull() {
        CodesListMetadataDto dto = metadataMapper.toDto(null);
        assertNull(dto);
    }

    @Test
    void toDto_shouldMapFieldsAndExternalLink_whenProjectionHasExternalLink() {
        MetadataProjection projection = mock(MetadataProjection.class);
        UUID id = UUID.randomUUID();
        CodesListExternalLinkEntity externalLinkEntity = new CodesListExternalLinkEntity();

        when(projection.getId()).thenReturn(id);
        when(projection.getLabel()).thenReturn("Label1");
        when(projection.getVersion()).thenReturn(1);
        when(projection.getTheme()).thenReturn("COMMUNES");
        when(projection.getReferenceYear()).thenReturn("2024");
        when(projection.getCodesListExternalLink()).thenReturn(externalLinkEntity);
        when(projection.isDeprecated()).thenReturn(false);
        when(projection.isValid()).thenReturn(true);

        CodesListExternalLinkDto externalLinkDto = new CodesListExternalLinkDto("ExternalLink1");
        when(externalLinkMapper.toDto(externalLinkEntity)).thenReturn(externalLinkDto);

        CodesListMetadataDto dto = metadataMapper.toDto(projection);

        assertNotNull(dto);
        assertEquals(id, dto.id());
        assertEquals("Label1", dto.label());
        assertEquals(1, dto.version().intValue());
        assertEquals("COMMUNES", dto.theme());
        assertEquals("2024", dto.referenceYear());
        assertEquals(externalLinkDto, dto.externalLink());
        assertFalse(dto.isDeprecated());
        assertTrue(dto.isValid());

        verify(externalLinkMapper, times(1)).toDto(externalLinkEntity);
    }

    @Test
    void toDto_shouldMapFieldsAndSetExternalLinkNull_whenProjectionHasNoExternalLink() {
        MetadataProjection projection = mock(MetadataProjection.class);
        UUID id = UUID.randomUUID();

        when(projection.getId()).thenReturn(id);
        when(projection.getLabel()).thenReturn("Label1");
        when(projection.getVersion()).thenReturn(1);
        when(projection.getTheme()).thenReturn("COMMUNES");
        when(projection.getReferenceYear()).thenReturn("2024");
        when(projection.getCodesListExternalLink()).thenReturn(null);
        when(projection.isDeprecated()).thenReturn(false);
        when(projection.isValid()).thenReturn(true);


        CodesListMetadataDto dto = metadataMapper.toDto(projection);

        assertNotNull(dto);
        assertEquals(id, dto.id());
        assertEquals("Label1", dto.label());
        assertEquals(1, dto.version().intValue());
        assertEquals("COMMUNES", dto.theme());
        assertEquals("2024", dto.referenceYear());
        assertNull(dto.externalLink());
        assertFalse(dto.isDeprecated());
        assertTrue(dto.isValid());

        verify(externalLinkMapper, never()).toDto(any());
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
        when(projection.getCodesListExternalLink()).thenReturn(null);
        when(projection.isDeprecated()).thenReturn(false);
        when(projection.isValid()).thenReturn(true);
        when(projection.getSearchConfiguration()).thenReturn(new SearchConfig(Map.of("enabled", true)));

        List<CodesListMetadataExpandableFieldsEnum> expand = List.of(CodesListMetadataExpandableFieldsEnum.SEARCH_CONFIGURATION);
        CodesListMetadataDto dto = metadataMapper.toDto(projection, expand);

        assertNotNull(dto);
        assertEquals(new SearchConfig(Map.of("enabled", true)), dto.searchConfiguration());

        verify(externalLinkMapper, never()).toDto(any());
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
        when(projection.getCodesListExternalLink()).thenReturn(null);
        when(projection.isDeprecated()).thenReturn(false);
        when(projection.isValid()).thenReturn(true);
        when(projection.getSearchConfiguration()).thenReturn(new SearchConfig(Map.of("enabled", true)));

        List<CodesListMetadataExpandableFieldsEnum> expand = List.of();
        CodesListMetadataDto dto = metadataMapper.toDto(projection, expand);

        assertNotNull(dto);
        assertNull(dto.searchConfiguration());

        verify(externalLinkMapper, never()).toDto(any());
    }
}