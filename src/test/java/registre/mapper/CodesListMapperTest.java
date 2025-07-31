package registre.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import registre.dto.CodesListDto;
import registre.dto.CodesListExternalLinkDto;
import registre.dto.MetadataDto;
import registre.entity.CodesListEntity;

import static org.junit.jupiter.api.Assertions.*;

class CodesListMapperTest {

    private CodesListMapper codesListMapper;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        codesListMapper = new CodesListMapper();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testToDto_WithValidEntity() throws Exception {
        // Given
        CodesListEntity codesListEntity = new CodesListEntity();
        codesListEntity.setId("CodesList1");
        codesListEntity.setMetadataLabel("LabelCodesList1");
        codesListEntity.setMetadataVersion("v1");
        codesListEntity.setExternalLinkVersion("ext-v1");

        JsonNode searchConfig = objectMapper.readTree("{\"enabled\": true}");
        JsonNode content = objectMapper.readTree("[{\"code\": \"01\"}]");

        codesListEntity.setSearchConfiguration(searchConfig);
        codesListEntity.setContent(content);

        // When
        CodesListDto dto = codesListMapper.toDto(codesListEntity);

        // Then
        assertNotNull(dto);
        assertEquals("CodesList1", dto.getId());
        assertNotNull(dto.getMetadata());
        assertEquals("LabelCodesList1", dto.getMetadata().getLabel());
        assertEquals("v1", dto.getMetadata().getVersion());
        assertNotNull(dto.getMetadata().getExternalLink());
        assertEquals("ext-v1", dto.getMetadata().getExternalLink().getVersion());
        assertTrue(dto.getSearchConfiguration().get("enabled").asBoolean());
        assertEquals("01", dto.getContent().get(0).get("code").asText());
    }

    @Test
    void testToEntity_WithValidDto() throws Exception {
        // Given
        CodesListDto dto = new CodesListDto();
        dto.setId("CodesList2");

        MetadataDto metadata = new MetadataDto();
        metadata.setLabel("LabelCodesList2");
        metadata.setVersion("v2");

        CodesListExternalLinkDto externalLink = new CodesListExternalLinkDto();
        externalLink.setVersion("ext-v2");
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
        assertEquals("CodesList2", entity.getId());
        assertEquals("LabelCodesList2", entity.getMetadataLabel());
        assertEquals("v2", entity.getMetadataVersion());
        assertEquals("ext-v2", entity.getExternalLinkVersion());
        assertFalse(entity.getSearchConfiguration().get("enabled").asBoolean());
        assertEquals("01", entity.getContent().get(0).get("code").asText());
    }
}
