package registre.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import registre.dto.Metadata;
import registre.dto.CodesListExternalLink;
import registre.entity.MetadataEntity;
import registre.entity.CodesListExternalLinkEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MetadataMapperTest {

    private CodesListExternalLinkMapper externalLinkMapper;
    private MetadataMapper metadataMapper;

    @BeforeEach
    void setUp() {
        externalLinkMapper = mock(CodesListExternalLinkMapper.class);
        metadataMapper = new MetadataMapper(externalLinkMapper);
    }

    @Test
    void testToEntity_WithValidDto() {
        // Given
        UUID uuid = UUID.randomUUID();
        Metadata dto = new Metadata();
        dto.setId(uuid);
        dto.setLabel("Label1");
        dto.setVersion("v1");

        CodesListExternalLink externalLinkDto = new CodesListExternalLink();
        CodesListExternalLinkEntity externalLinkEntity = new CodesListExternalLinkEntity();
        dto.setExternalLink(externalLinkDto);
        when(externalLinkMapper.toEntity(externalLinkDto)).thenReturn(externalLinkEntity);

        // When
        MetadataEntity entity = metadataMapper.toEntity(dto);

        // Then
        assertNotNull(entity);
        assertEquals(uuid, entity.getId());
        assertEquals("Label1", entity.getLabel());
        assertEquals("v1", entity.getVersion());
        assertEquals(externalLinkEntity, entity.getExternalLink());
    }

    @Test
    void testToEntity_WithNullDto() {
        assertNull(metadataMapper.toEntity(null));
    }

    @Test
    void testToDto_WithValidEntity() {
        // Given
        UUID uuid = UUID.randomUUID();
        MetadataEntity entity = new MetadataEntity();
        entity.setId(uuid);
        entity.setLabel("Label2");
        entity.setVersion("v2");

        CodesListExternalLinkEntity externalLinkEntity = new CodesListExternalLinkEntity();
        CodesListExternalLink externalLinkDto = new CodesListExternalLink();
        entity.setExternalLink(externalLinkEntity);
        when(externalLinkMapper.toDto(externalLinkEntity)).thenReturn(externalLinkDto);

        // When
        Metadata dto = metadataMapper.toDto(entity);

        // Then
        assertNotNull(dto);
        assertEquals(uuid, dto.getId());
        assertEquals("Label2", dto.getLabel());
        assertEquals("v2", dto.getVersion());
        assertEquals(externalLinkDto, dto.getExternalLink());
    }

    @Test
    void testToDto_WithNullEntity() {
        assertNull(metadataMapper.toDto(null));
    }
}
