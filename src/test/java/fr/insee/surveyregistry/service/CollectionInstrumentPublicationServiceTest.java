package fr.insee.surveyregistry.service;

import fr.insee.surveyregistry.entity.CollectionInstrumentEntity;
import fr.insee.surveyregistry.entity.ConceptualModelEntity;
import fr.insee.surveyregistry.enums.CollectionInstrumentMode;
import fr.insee.surveyregistry.enums.CollectionInstrumentType;
import fr.insee.surveyregistry.repository.CollectionInstrumentRepository;
import fr.insee.surveyregistry.repository.ConceptualModelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CollectionInstrumentPublicationServiceTest {

    private CollectionInstrumentRepository repository;
    private ConceptualModelRepository conceptualModelRepository;
    private CollectionInstrumentPublicationService service;

    @BeforeEach
    void setUp() {
        repository = mock(CollectionInstrumentRepository.class);
        conceptualModelRepository = mock(ConceptualModelRepository.class);
        service = new CollectionInstrumentPublicationService(repository, conceptualModelRepository);
    }

    @Test
    void testCreateCollectionInstrumentMetadataOnly() {
        UUID modelId = UUID.randomUUID();

        ConceptualModelEntity model = new ConceptualModelEntity();
        model.setPoguesVersionId(modelId);

        when(conceptualModelRepository.findById(modelId)).thenReturn(Optional.of(model));

        UUID result = service.createCollectionInstrumentMetadataOnly(
                CollectionInstrumentMode.CAWI,
                CollectionInstrumentType.JSON,
                modelId
        );

        assertNotNull(result);
        verify(repository).save(any(CollectionInstrumentEntity.class));
    }

    @Test
    void testCreateCollectionInstrumentMetadataOnly_ModelNotFound() {
        UUID modelId = UUID.randomUUID();

        when(conceptualModelRepository.findById(modelId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> service.createCollectionInstrumentMetadataOnly(
                        CollectionInstrumentMode.CAWI,
                        CollectionInstrumentType.JSON,
                        modelId
                ));
    }

    @Test
    void testCreateCollectionInstrument_WithContent() {
        UUID modelId = UUID.randomUUID();

        ConceptualModelEntity model = new ConceptualModelEntity();
        model.setPoguesVersionId(modelId);

        when(conceptualModelRepository.findById(modelId)).thenReturn(Optional.of(model));

        UUID result = service.createCollectionInstrument(
                CollectionInstrumentMode.CATI,
                CollectionInstrumentType.XML,
                modelId,
                "content"
        );

        assertNotNull(result);
        verify(repository).save(any(CollectionInstrumentEntity.class));
    }

    @Test
    void testCreateContent() {
        UUID id = UUID.randomUUID();

        CollectionInstrumentEntity entity = new CollectionInstrumentEntity();

        when(repository.existsById(id)).thenReturn(true);
        when(repository.existsByCollectionInstrumentIdAndContentIsNotNull(id)).thenReturn(false);
        when(repository.findById(id)).thenReturn(Optional.of(entity));

        service.createContent(id, "new content");

        assertEquals("new content", entity.getContent());
        verify(repository).save(entity);
    }

    @Test
    void testCreateContent_NotFound() {
        UUID id = UUID.randomUUID();

        when(repository.existsById(id)).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> service.createContent(id, "content"));
    }

    @Test
    void testCreateContent_AlreadyExists() {
        UUID id = UUID.randomUUID();

        when(repository.existsById(id)).thenReturn(true);
        when(repository.existsByCollectionInstrumentIdAndContentIsNotNull(id)).thenReturn(true);

        assertThrows(ResponseStatusException.class,
                () -> service.createContent(id, "content"));
    }
}