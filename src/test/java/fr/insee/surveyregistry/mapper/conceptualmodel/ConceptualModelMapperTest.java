package fr.insee.surveyregistry.mapper.conceptualmodel;

import fr.insee.surveyregistry.dto.conceptualmodel.ConceptualModelDto;
import fr.insee.surveyregistry.entity.ConceptualModelEntity;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ConceptualModelMapperTest {

    private final ConceptualModelMapper conceptualModelMapper = new ConceptualModelMapper();

    @Test
    void testToDto_WithValidEntityWithoutDdiContent() {
        // Given
        UUID poguesVersionId = UUID.randomUUID();
        ConceptualModelEntity entity = new ConceptualModelEntity();

        entity.setPoguesVersionId(poguesVersionId);
        entity.setPoguesId("mquod4mj");
        entity.setSerieId("s1193");
        entity.setDdiContent(null);

        // When
        ConceptualModelDto dto = conceptualModelMapper.toDto(entity);

        // Then
        assertNotNull(dto);
        assertEquals(poguesVersionId, dto.poguesVersionId());

        assertNotNull(dto.metadata());
        assertEquals(poguesVersionId, dto.metadata().poguesVersionId());
        assertEquals("mquod4mj", dto.metadata().poguesId());
        assertEquals("s1193", dto.metadata().serieId());

        assertNull(dto.ddiContent());
    }

    @Test
    void testToDto_WithValidEntityWithDdiContent() {
        // Given
        UUID poguesVersionId = UUID.randomUUID();
        ConceptualModelEntity entity = new ConceptualModelEntity();

        entity.setPoguesVersionId(poguesVersionId);
        entity.setPoguesId("mquod4mj");
        entity.setSerieId("s1193");
        entity.setDdiContent("<DDIInstance></DDIInstance>");

        // When
        ConceptualModelDto dto = conceptualModelMapper.toDto(entity);

        // Then
        assertNotNull(dto);
        assertEquals(poguesVersionId, dto.poguesVersionId());

        assertNotNull(dto.metadata());
        assertEquals("mquod4mj", dto.metadata().poguesId());
        assertEquals("s1193", dto.metadata().serieId());

        assertEquals("<DDIInstance></DDIInstance>", dto.ddiContent());
    }

    @Test
    void testToDto_WithNullEntity() {
        // When
        ConceptualModelDto dto = conceptualModelMapper.toDto(null);

        // Then
        assertNull(dto);
    }
}
