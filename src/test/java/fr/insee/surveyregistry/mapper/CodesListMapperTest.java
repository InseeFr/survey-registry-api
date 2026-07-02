package fr.insee.surveyregistry.mapper;

import fr.insee.surveyregistry.dto.CodesListContent;
import fr.insee.surveyregistry.dto.CodesListDto;
import fr.insee.surveyregistry.dto.CodesListMetadataDto;
import fr.insee.surveyregistry.dto.SearchConfig;
import fr.insee.surveyregistry.entity.CodesListEntity;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CodesListMapperTest {

    private final CodesListMapper codesListMapper = new CodesListMapper();

    @Test
    void testToDto_WithValidEntity() {
        // Given
        CodesListEntity codesListEntity = new CodesListEntity();
        UUID testId = UUID.randomUUID();
        codesListEntity.setId(testId);
        codesListEntity.setLabel("Label1");
        codesListEntity.setVersion(1);
        codesListEntity.setTheme("COMMUNES");
        codesListEntity.setReferenceYear("2024");
        codesListEntity.setUrn("urn:ddi:communes:2024:1");
        codesListEntity.setDeprecated(false);
        codesListEntity.setValid(true);

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
        assertEquals(1, dto.metadata().version().intValue());
        assertEquals("COMMUNES", dto.metadata().theme());
        assertEquals("2024", dto.metadata().referenceYear());
        assertEquals("urn:ddi:communes:2024:1", dto.metadata().urn());
        assertFalse(dto.metadata().isDeprecated());
        assertTrue(dto.metadata().isValid());

        assertNotNull(dto.searchConfiguration());
        assertEquals(true, dto.searchConfiguration().content().get("enabled"));
        assertNotNull(dto.content());
        assertEquals("01", dto.content().items().getFirst().get("code"));
    }

    @Test
    void testToEntity_WithValidDto() {
        // Given
        UUID testId = UUID.randomUUID();

        CodesListMetadataDto metadata = new CodesListMetadataDto(testId, "Label2", 2, "COMMUNES", "2024", "urn:ddi:communes:2024:1", false, true, null);

        CodesListDto dto = new CodesListDto(testId, metadata, new SearchConfig(Map.of("enabled", false)),
                new CodesListContent(List.of(Map.of("code", "01"))));

        // When
        CodesListEntity entity = codesListMapper.toEntity(dto);

        // Then
        assertNotNull(entity);
        assertEquals(testId, entity.getId());
        assertEquals("Label2", entity.getLabel());
        assertEquals(2, entity.getVersion().intValue());
        assertEquals("COMMUNES", entity.getTheme());
        assertEquals("2024", entity.getReferenceYear());
        assertEquals("urn:ddi:communes:2024:1", entity.getUrn());
        assertFalse(entity.isDeprecated());
        assertTrue(entity.isValid());
        // searchConfiguration and content are not set in toEntity()
        assertNull(entity.getSearchConfiguration());
        assertNull(entity.getContent());
    }

    @Test
    void testToEntity_WithNullBooleans() {
        // Given
        UUID id = UUID.randomUUID();

        CodesListMetadataDto metadata = new CodesListMetadataDto(
                id,
                "Label",
                1,
                "COMMUNES",
                "2024",
                "urn:ddi:communes:2024:1",
                null,
                null,
                null
        );

        CodesListDto dto = new CodesListDto(id, metadata, null, null);

        // When
        CodesListEntity entity = codesListMapper.toEntity(dto);

        // Then
        assertNotNull(entity);
        assertFalse(entity.isDeprecated()); // null → false
        assertTrue(entity.isValid());       // null → true
    }
}
