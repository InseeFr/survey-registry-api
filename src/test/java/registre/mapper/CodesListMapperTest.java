package registre.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import registre.dto.CodesListDto;
import registre.dto.CodesListExternalLinkDto;
import registre.dto.MetadataDto;
import registre.entity.CodesListEntity;
import registre.entity.CodesListExternalLinkEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CodesListMapperTest {

    private CodesListMapper codesListMapper;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        codesListMapper = new CodesListMapper(new CodesListExternalLinkMapper());
        objectMapper = new ObjectMapper();
    }

    @Test
    void testToDto_WithValidEntity() throws Exception {
        // Given
        CodesListEntity codesListEntity = new CodesListEntity();
        UUID testId = UUID.randomUUID();
        codesListEntity.setId(testId);
        codesListEntity.setLabel("Label1");
        codesListEntity.setVersion("v1");

        CodesListExternalLinkEntity externalLink = new CodesListExternalLinkEntity();
        externalLink.setId("ExternalLink1");
        externalLink.setVersion("v1");
        codesListEntity.setCodesListExternalLink(externalLink);

        JsonNode searchConfig = objectMapper.readTree("{\"enabled\": true}");
        JsonNode content = objectMapper.readTree("[{\"code\": \"01\"}]");

        codesListEntity.setSearchConfiguration(searchConfig);
        codesListEntity.setContent(content);

        // When
        CodesListDto dto = codesListMapper.toDto(codesListEntity);

        // Then
        assertNotNull(dto);
        assertEquals(testId, dto.getId());
        assertNotNull(dto.getMetadata());
        assertEquals(testId, dto.getMetadata().getId());
        assertEquals("Label1", dto.getMetadata().getLabel());
        assertEquals("v1", dto.getMetadata().getVersion());
        assertNotNull(dto.getMetadata().getExternalLink());
        assertEquals("ExternalLink1", dto.getMetadata().getExternalLink().getId());
        assertEquals("v1", dto.getMetadata().getExternalLink().getVersion());
        assertTrue(dto.getSearchConfiguration().get("enabled").asBoolean());
        assertEquals("01", dto.getContent().get(0).get("code").asText());
    }

    @Test
    void testToEntity_WithValidDto() throws Exception {
        // Given
        CodesListDto dto = new CodesListDto();
        UUID testId = UUID.randomUUID();
        dto.setId(testId);

        MetadataDto metadata = new MetadataDto();
        metadata.setLabel("Label2");
        metadata.setVersion("v2");

        CodesListExternalLinkDto externalLink = new CodesListExternalLinkDto();
        externalLink.setVersion("v2");
        metadata.setExternalLink(externalLink);

        dto.setMetadata(metadata);

        JsonNode searchConfig = objectMapper.readTree("{\"enabled\": false}");
        JsonNode content = objectMapper.readTree("[{\"code\": \"01\"}]");

        dto.setSearchConfiguration(searchConfig);
        dto.setContent(content);

        // When
        CodesListEntity entity = codesListMapper.toEntity(dto);

        // Then
        assertNotNull(entity);
        assertEquals(testId, entity.getId());
        assertEquals("Label2", entity.getLabel());
        assertEquals("v2", entity.getVersion());
        assertEquals("v2", entity.getCodesListExternalLink().getVersion());
        assertFalse(entity.getSearchConfiguration().get("enabled").asBoolean());
        assertEquals("01", entity.getContent().get(0).get("code").asText());
    }
}
