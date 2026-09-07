package fr.insee.surveyregistry.mapper.conceptualmodel;

import fr.insee.surveyregistry.dto.conceptualmodel.ConceptualModelMetadataDto;
import fr.insee.surveyregistry.entity.ConceptualModelEntity;
import fr.insee.surveyregistry.repository.ConceptualModelRepository;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ConceptualModelMetadataMapperTest {

    private final ConceptualModelMetadataMapper conceptualModelMetadataMapper =
            new ConceptualModelMetadataMapper();

    @Test
    void testToDto_WithValidProjection() {
        // Given
        UUID poguesVersionId = UUID.randomUUID();

        ConceptualModelRepository.MetadataProjection projection =
                mock(ConceptualModelRepository.MetadataProjection.class);

        when(projection.getPoguesVersionId())
                .thenReturn(poguesVersionId);
        when(projection.getPoguesId())
                .thenReturn("mquod4mj");
        when(projection.getSerieId())
                .thenReturn("s1193");

        // When
        ConceptualModelMetadataDto dto =
                conceptualModelMetadataMapper.toDto(projection);

        // Then
        assertNotNull(dto);
        assertEquals(poguesVersionId, dto.poguesVersionId());
        assertEquals("mquod4mj", dto.poguesId());
        assertEquals("s1193", dto.serieId());
    }


    @Test
    void testToEntity_WithValidDto() {
        // Given
        UUID poguesVersionId = UUID.randomUUID();

        ConceptualModelMetadataDto dto =
                new ConceptualModelMetadataDto(
                        poguesVersionId,
                        "mquod4mj",
                        "s1193"
                );

        // When
        ConceptualModelEntity entity = conceptualModelMetadataMapper.toEntity(dto);

        // Then
        assertNotNull(entity);
        assertEquals(poguesVersionId, entity.getPoguesVersionId());
        assertEquals("mquod4mj", entity.getPoguesId());
        assertEquals("s1193", entity.getSerieId());
    }


    @Test
    void testToDto_WithNullProjection() {
        // When
        ConceptualModelMetadataDto dto =
                conceptualModelMetadataMapper.toDto(null);

        // Then
        assertNull(dto);
    }


    @Test
    void testToEntity_WithNullDto() {
        // When
        ConceptualModelEntity entity =
                conceptualModelMetadataMapper.toEntity(null);

        // Then
        assertNull(entity);
    }
}