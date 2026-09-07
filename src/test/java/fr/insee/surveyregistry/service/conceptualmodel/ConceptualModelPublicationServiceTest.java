package fr.insee.surveyregistry.service.conceptualmodel;

import fr.insee.surveyregistry.dto.conceptualmodel.ConceptualModelDto;
import fr.insee.surveyregistry.dto.conceptualmodel.ConceptualModelMetadataDto;
import fr.insee.surveyregistry.entity.ConceptualModelEntity;
import fr.insee.surveyregistry.exception.ResourceAlreadyExistsException;
import fr.insee.surveyregistry.exception.ResourceNotFoundException;
import fr.insee.surveyregistry.mapper.conceptualmodel.ConceptualModelMapper;
import fr.insee.surveyregistry.mapper.conceptualmodel.ConceptualModelMetadataMapper;
import fr.insee.surveyregistry.repository.ConceptualModelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConceptualModelPublicationServiceTest {

    private ConceptualModelRepository repository;
    private ConceptualModelMetadataMapper conceptualModelMetadataMapper;
    private ConceptualModelMapper conceptualModelMapper;
    private ConceptualModelPublicationService service;

    @BeforeEach
    void setUp() {
        repository = mock(ConceptualModelRepository.class);
        conceptualModelMetadataMapper = mock(ConceptualModelMetadataMapper.class);
        conceptualModelMapper = mock(ConceptualModelMapper.class);

        service = new ConceptualModelPublicationService(
                repository,
                conceptualModelMetadataMapper,
                conceptualModelMapper
        );
    }

    @Test
    void testCreate() {
        // Given
        UUID poguesVersionId = UUID.randomUUID();

        ConceptualModelMetadataDto metadataDto =
                new ConceptualModelMetadataDto(
                        poguesVersionId,
                        "mquod4mj",
                        "s1193"
                );

        ConceptualModelEntity entity = new ConceptualModelEntity();
        entity.setPoguesVersionId(poguesVersionId);
        entity.setPoguesId("mquod4mj");
        entity.setSerieId("s1193");

        ConceptualModelDto dto =
                new ConceptualModelDto(
                        poguesVersionId,
                        metadataDto,
                        null
                );

        when(repository.existsById(poguesVersionId)).thenReturn(false);
        when(conceptualModelMetadataMapper.toEntity(metadataDto)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(conceptualModelMapper.toDto(entity)).thenReturn(dto);

        // When
        ConceptualModelDto result = service.create(metadataDto);

        // Then
        assertNotNull(result);
        assertEquals(poguesVersionId, result.poguesVersionId());
        assertEquals("mquod4mj", result.metadata().poguesId());
        assertEquals("s1193", result.metadata().serieId());
        assertNull(result.ddiContent());

        verify(repository).existsById(poguesVersionId);
        verify(conceptualModelMetadataMapper).toEntity(metadataDto);
        verify(repository).save(entity);
        verify(conceptualModelMapper).toDto(entity);
    }

    @Test
    void testCreate_AlreadyExists() {
        // Given
        UUID poguesVersionId = UUID.randomUUID();

        ConceptualModelMetadataDto metadataDto =
                new ConceptualModelMetadataDto(
                        poguesVersionId,
                        "mquod4mj",
                        "s1193"
                );

        when(repository.existsById(poguesVersionId)).thenReturn(true);

        // When / Then
        assertThrows(
                ResourceAlreadyExistsException.class,
                () -> service.create(metadataDto)
        );

        verify(repository).existsById(poguesVersionId);
        verify(repository, never()).save(any());
        verify(conceptualModelMetadataMapper, never()).toEntity(any());
        verify(conceptualModelMapper, never()).toDto(any());
    }

    @Test
    void testAddDdiContent() {
        // Given
        UUID poguesVersionId = UUID.randomUUID();

        ConceptualModelEntity entity = new ConceptualModelEntity();
        entity.setPoguesVersionId(poguesVersionId);
        entity.setPoguesId("mquod4mj");
        entity.setSerieId("s1193");
        entity.setDdiContent(null);

        String ddiContent = "<DDIInstance></DDIInstance>";

        when(repository.findById(poguesVersionId))
                .thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(entity);

        // When
        service.addDdiContent(poguesVersionId, ddiContent);

        // Then
        assertEquals(ddiContent, entity.getDdiContent());

        verify(repository).findById(poguesVersionId);
        verify(repository).save(entity);
    }

    @Test
    void testAddDdiContent_NotFound() {
        // Given
        UUID poguesVersionId = UUID.randomUUID();

        when(repository.findById(poguesVersionId))
                .thenReturn(Optional.empty());

        // When / Then
        assertThrows(
                ResourceNotFoundException.class,
                () -> service.addDdiContent(
                        poguesVersionId,
                        "<DDIInstance></DDIInstance>"
                )
        );

        verify(repository).findById(poguesVersionId);
        verify(repository, never()).save(any());
    }

    @Test
    void testAddDdiContent_AlreadyExists() {
        // Given
        UUID poguesVersionId = UUID.randomUUID();

        ConceptualModelEntity entity = new ConceptualModelEntity();
        entity.setPoguesVersionId(poguesVersionId);
        entity.setPoguesId("mquod4mj");
        entity.setSerieId("s1193");
        entity.setDdiContent("<DDIInstance>existing</DDIInstance>");

        when(repository.findById(poguesVersionId))
                .thenReturn(Optional.of(entity));

        // When / Then
        assertThrows(
                ResourceAlreadyExistsException.class,
                () -> service.addDdiContent(
                        poguesVersionId,
                        "<DDIInstance>new</DDIInstance>"
                )
        );

        verify(repository).findById(poguesVersionId);
        verify(repository, never()).save(any());
    }
}