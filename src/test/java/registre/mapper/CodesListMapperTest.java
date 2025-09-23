package registre.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import registre.dto.CodesListDto;
import registre.dto.CodesListExternalLinkDto;
import registre.dto.MetadataDto;
import registre.entity.CodesListEntity;
import registre.entity.CodesListExternalLinkEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class CodesListMapperTest {

    private CodesListMapper codesListMapper;

    @BeforeEach
    void setUp() {
        codesListMapper = new CodesListMapper(new CodesListExternalLinkMapper());
    }

    @Test
    void testToDto_WithValidEntity() {
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

        Map<String,Object> searchConfig = Map.of("enabled", true);
        List<Map<String,Object>> content = List.of(Map.of("code", "01"));

        codesListEntity.setSearchConfiguration(searchConfig);
        codesListEntity.setContent(content);

        // When
        CodesListDto dto = codesListMapper.toDto(codesListEntity);

        // Then
        assertNotNull(dto);
        assertEquals(testId, dto.id());
        assertNotNull(dto.metadata());
        assertEquals(testId, dto.metadata().id());
        assertEquals("Label1", dto.metadata().label());
        assertEquals("v1", dto.metadata().version());
        assertNotNull(dto.metadata().externalLink());
        assertEquals("ExternalLink1", dto.metadata().externalLink().id());

        assertNotNull(dto.searchConfiguration());
        assertEquals(true, dto.searchConfiguration().get("enabled"));
        assertEquals("01", dto.content().getFirst().get("code"));
    }

    @Test
    void testToEntity_WithValidDto() {
        // Given
        String testId1 = UUID.randomUUID().toString();
        UUID testId2 = UUID.randomUUID();

        CodesListExternalLinkDto externalLink = new CodesListExternalLinkDto(testId1);

        MetadataDto metadata = new MetadataDto(testId2, "Label2", "v2", externalLink);

        Map<String,Object> searchConfig = Map.of("enabled", false);
        List<Map<String,Object>> content = List.of(Map.of("code", "01"));

        CodesListDto dto = new CodesListDto(testId2, metadata, searchConfig, content);

        // When
        CodesListEntity entity = codesListMapper.toEntity(dto);

        // Then
        assertNotNull(entity);
        assertEquals(testId2, entity.getId());
        assertEquals("Label2", entity.getLabel());
        assertEquals("v2", entity.getVersion());
        // searchConfiguration and content are not set in toEntity()
        assertNull(entity.getSearchConfiguration());
        assertNull(entity.getContent());
    }
}
