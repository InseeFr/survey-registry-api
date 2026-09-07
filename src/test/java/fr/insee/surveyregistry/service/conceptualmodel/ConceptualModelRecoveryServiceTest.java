package fr.insee.surveyregistry.service.conceptualmodel;

import fr.insee.surveyregistry.dto.conceptualmodel.ConceptualModelMetadataDto;
import fr.insee.surveyregistry.entity.ConceptualModelEntity;
import fr.insee.surveyregistry.exception.ResourceNotFoundException;
import fr.insee.surveyregistry.mapper.conceptualmodel.ConceptualModelMetadataMapper;
import fr.insee.surveyregistry.repository.ConceptualModelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConceptualModelRecoveryServiceTest {

    private ConceptualModelRepository repository;
    private ConceptualModelMetadataMapper mapper;
    private ConceptualModelRecoveryService service;

    @BeforeEach
    void setUp() {
        repository = mock(ConceptualModelRepository.class);
        mapper = mock(ConceptualModelMetadataMapper.class);

        service = new ConceptualModelRecoveryService(repository, mapper);
    }

    @Test
    void testGetByPoguesVersionId_Found() {
        // Given
        UUID poguesVersionId = UUID.randomUUID();

        ConceptualModelRepository.MetadataProjection projection =
                mock(ConceptualModelRepository.MetadataProjection.class);

        ConceptualModelMetadataDto dto =
                new ConceptualModelMetadataDto(
                        poguesVersionId,
                        "mquod4mj",
                        "s1193"
                );

        when(repository.findMetadataByPoguesVersionId(poguesVersionId))
                .thenReturn(Optional.of(projection));

        when(mapper.toDto(projection)).thenReturn(dto);

        // When
        ConceptualModelMetadataDto result = service.getByPoguesVersionId(poguesVersionId);

        // Then
        assertNotNull(result);
        assertEquals(poguesVersionId, result.poguesVersionId());
        assertEquals("mquod4mj", result.poguesId());
        assertEquals("s1193", result.serieId());

        verify(repository).findMetadataByPoguesVersionId(poguesVersionId);
        verify(mapper).toDto(projection);
    }

    @Test
    void testGetByPoguesVersionId_NotFound() {
        // Given
        UUID poguesVersionId = UUID.randomUUID();

        when(repository.findMetadataByPoguesVersionId(poguesVersionId))
                .thenReturn(Optional.empty());

        // When / Then
        assertThrows(
                ResourceNotFoundException.class,
                () -> service.getByPoguesVersionId(poguesVersionId)
        );

        verify(repository).findMetadataByPoguesVersionId(poguesVersionId);
        verify(mapper, never()).toDto(any());
    }

    @Test
    void testGetDDIByPoguesVersionId_Found() {
        // Given
        UUID poguesVersionId = UUID.randomUUID();
        String ddiContent = "<DDIInstance>content</DDIInstance>";
        ConceptualModelEntity entity = new ConceptualModelEntity();
        entity.setPoguesVersionId(poguesVersionId);
        entity.setDdiContent(ddiContent);

        when(repository.findById(poguesVersionId))
                .thenReturn(Optional.of(entity));

        // When
        String result = service.getDDIByPoguesVersionId(poguesVersionId);

        // Then
        assertEquals(ddiContent, result);

        verify(repository).findById(poguesVersionId);
    }

    @Test
    void testGetDDIByPoguesVersionId_NotFound() {
        // Given
        UUID poguesVersionId = UUID.randomUUID();

        when(repository.findById(poguesVersionId))
                .thenReturn(Optional.empty());

        // When / Then
        assertThrows(
                ResourceNotFoundException.class,
                () -> service.getDDIByPoguesVersionId(poguesVersionId)
        );

        verify(repository).findById(poguesVersionId);
    }
}
