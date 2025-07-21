package registre.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import registre.dto.CodesListExternalLink;
import registre.entity.CodesListExternalLinkEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CodesListExternalLinkMapperTest {

    private CodesListExternalLinkMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CodesListExternalLinkMapper();
    }

    @Test
    void testToEntity_WithValidDto() {
        // Given
        UUID uuid = UUID.randomUUID();
        CodesListExternalLink dto = new CodesListExternalLink();
        dto.setUuid(uuid);
        dto.setVersion("v1");

        // When
        CodesListExternalLinkEntity entity = mapper.toEntity(dto);

        // Then
        assertNotNull(entity);
        assertEquals(uuid, entity.getUuid());
        assertEquals("v1", entity.getVersion());
    }

    @Test
    void testToEntity_WithNullDto() {
        assertNull(mapper.toEntity(null));
    }

    @Test
    void testToDto_WithValidEntity() {
        // Given
        UUID uuid = UUID.randomUUID();
        CodesListExternalLinkEntity entity = new CodesListExternalLinkEntity();
        entity.setUuid(uuid);
        entity.setVersion("v2");

        // When
        CodesListExternalLink dto = mapper.toDto(entity);

        // Then
        assertNotNull(dto);
        assertEquals(uuid, dto.getUuid());
        assertEquals("v2", dto.getVersion());
    }

    @Test
    void testToDto_WithNullEntity() {
        assertNull(mapper.toDto(null));
    }
}
