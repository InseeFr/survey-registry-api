package fr.insee.surveyregistry.mapper.conceptualmodel;

import fr.insee.surveyregistry.dto.conceptualmodel.ConceptualModelDto;
import fr.insee.surveyregistry.entity.ConceptualModelEntity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConceptualModelMapperTest {

    private final ConceptualModelMapper conceptualModelMapper = new ConceptualModelMapper();

    @Test
    void testToDto_WithValidEntity() {
        // Given
        ConceptualModelEntity entity = new ConceptualModelEntity();

        entity.setPoguesId("mquod4mj");
        entity.setSerieId("s1193");

        // When
        ConceptualModelDto dto = conceptualModelMapper.toDto(entity);

        // Then
        assertNotNull(dto);
        assertEquals("mquod4mj", dto.poguesId());
        assertEquals("s1193", dto.serieId());
    }

    @Test
    void testToEntity_WithValidDto() {
        // Given
        ConceptualModelDto dto = new ConceptualModelDto("mquod4mj","s1193");

        // When
        ConceptualModelEntity entity = conceptualModelMapper.toEntity(dto);

        // Then
        assertNotNull(entity);
        assertEquals("mquod4mj", entity.getPoguesId());
        assertEquals("s1193", entity.getSerieId());
    }

    @Test
    void testToDto_WithNullEntity() {
        // When
        ConceptualModelDto dto = conceptualModelMapper.toDto(null);

        // Then
        assertNull(dto);
    }

    @Test
    void testToEntity_WithNullDto() {
        // When
        ConceptualModelEntity entity = conceptualModelMapper.toEntity(null);

        // Then
        assertNull(entity);
    }
}
