package registre.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import registre.dto.CodesListExternalLinkDto;
import registre.entity.CodesListExternalLinkEntity;

import static org.junit.jupiter.api.Assertions.*;

class CodesListExternalLinkMapperTest {

    private CodesListExternalLinkMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CodesListExternalLinkMapper();
    }

    @Test
    void testToEntity() {
        CodesListExternalLinkDto dto = new CodesListExternalLinkDto("ExternalLink1", "v1");

        CodesListExternalLinkEntity entity = mapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals("ExternalLink1", entity.getId());
        assertEquals("v1", entity.getVersion());
    }

    @Test
    void testToEntity_NullInput() {
        CodesListExternalLinkEntity entity = mapper.toEntity(null);
        assertNull(entity);
    }

    @Test
    void testToDto() {
        CodesListExternalLinkEntity entity = new CodesListExternalLinkEntity();
        entity.setId("ExternalLink2");
        entity.setVersion("v2");

        CodesListExternalLinkDto dto = mapper.toDto(entity);

        assertNotNull(dto);
        assertEquals("ExternalLink2", dto.id());
        assertEquals("v2", dto.version());
    }

    @Test
    void testToDto_NullInput() {
        CodesListExternalLinkDto dto = mapper.toDto(null);
        assertNull(dto);
    }
}
