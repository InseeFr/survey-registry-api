package registre.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import registre.dto.*;
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

        codesListEntity.setSearchConfiguration(new SearchConfig(Map.of("enabled", true)));
        codesListEntity.setContent(new CodesListContent(List.of(Map.of("code","01"))));

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
        assertEquals(true, dto.searchConfiguration().content().get("enabled"));
        assertNotNull(dto.content());
        assertEquals("01", dto.content().items().getFirst().get("code"));
    }

    @Test
    void testToEntity_WithValidDto() {
        // Given
        String testId1 = UUID.randomUUID().toString();
        UUID testId2 = UUID.randomUUID();

        CodesListExternalLinkDto externalLink = new CodesListExternalLinkDto(testId1);

        MetadataDto metadata = new MetadataDto(testId2, "Label2", "v2", externalLink);

        CodesListDto dto = new CodesListDto(testId2, metadata, new SearchConfig(Map.of("enabled", false)),
                new CodesListContent(List.of(Map.of("code", "01"))));

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
