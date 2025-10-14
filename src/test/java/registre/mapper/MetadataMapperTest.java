package registre.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import registre.dto.CodesListExternalLinkDto;
import registre.dto.MetadataDto;
import registre.entity.CodesListExternalLinkEntity;
import registre.repository.CodesListRepository.MetadataProjection;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MetadataMapperTest {

    private CodesListExternalLinkMapper externalLinkMapper;
    private MetadataMapper metadataMapper;

    @BeforeEach
    void setup() {
        externalLinkMapper = mock(CodesListExternalLinkMapper.class);
        metadataMapper = new MetadataMapper(externalLinkMapper);
    }

    @Test
    void toDto_shouldReturnNull_whenProjectionIsNull() {
        MetadataDto dto = metadataMapper.toDto(null);
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

        CodesListExternalLinkDto externalLinkDto = new CodesListExternalLinkDto("ExternalLink1");
        when(externalLinkMapper.toDto(externalLinkEntity)).thenReturn(externalLinkDto);

        MetadataDto dto = metadataMapper.toDto(projection);

        assertNotNull(dto);
        assertEquals(id, dto.id());
        assertEquals("Label1", dto.label());
        assertEquals(1, dto.version().intValue());
        assertEquals("COMMUNES", dto.theme());
        assertEquals("2024", dto.referenceYear());
        assertEquals(externalLinkDto, dto.externalLink());

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

        MetadataDto dto = metadataMapper.toDto(projection);

        assertNotNull(dto);
        assertEquals(id, dto.id());
        assertEquals("Label1", dto.label());
        assertEquals(1, dto.version().intValue());
        assertEquals("COMMUNES", dto.theme());
        assertEquals("2024", dto.referenceYear());
        assertNull(dto.externalLink());

        verify(externalLinkMapper, never()).toDto(any());
    }
}