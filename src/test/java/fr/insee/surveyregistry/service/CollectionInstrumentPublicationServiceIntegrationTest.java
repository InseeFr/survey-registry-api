package fr.insee.surveyregistry.service;

import fr.insee.surveyregistry.entity.CollectionInstrumentEntity;
import fr.insee.surveyregistry.entity.ConceptualModelEntity;
import fr.insee.surveyregistry.enums.CollectionInstrumentMode;
import fr.insee.surveyregistry.enums.CollectionInstrumentType;
import fr.insee.surveyregistry.repository.CollectionInstrumentRepository;
import fr.insee.surveyregistry.repository.ConceptualModelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CollectionInstrumentPublicationServiceIntegrationTest {

    @Autowired
    private CollectionInstrumentPublicationService service;

    @Autowired
    private CollectionInstrumentRepository repository;

    @Autowired
    private ConceptualModelRepository conceptualModelRepository;

    @BeforeEach
    void cleanDatabase() {
        repository.deleteAll();
        conceptualModelRepository.deleteAll();
    }

    private ConceptualModelEntity createConceptualModel() {
        ConceptualModelEntity model = new ConceptualModelEntity();
        model.setPoguesVersionId(UUID.randomUUID());
        model.setLabel("Test model");
        return conceptualModelRepository.save(model);
    }

    @Test
    void testCreateMetadataOnly() {
        ConceptualModelEntity model = createConceptualModel();

        UUID id = service.createCollectionInstrumentMetadataOnly(
                CollectionInstrumentMode.CAWI,
                CollectionInstrumentType.JSON,
                model.getPoguesVersionId()
        );

        CollectionInstrumentEntity entity = repository.findById(id).orElseThrow();

        assertEquals(CollectionInstrumentMode.CAWI, entity.getMode());
        assertEquals(CollectionInstrumentType.JSON, entity.getType());
        assertNull(entity.getContent());
    }

    @Test
    void testCreateFull() {
        ConceptualModelEntity model = createConceptualModel();

        UUID id = service.createCollectionInstrument(
                CollectionInstrumentMode.CATI,
                CollectionInstrumentType.XML,
                model.getPoguesVersionId(),
                "content"
        );

        CollectionInstrumentEntity entity = repository.findById(id).orElseThrow();

        assertEquals("content", entity.getContent());
    }

    @Test
    void testCreateContent() {
        ConceptualModelEntity model = createConceptualModel();

        UUID id = service.createCollectionInstrumentMetadataOnly(
                CollectionInstrumentMode.CAPI,
                CollectionInstrumentType.JSON,
                model.getPoguesVersionId()
        );

        service.createContent(id, "new content");

        CollectionInstrumentEntity entity = repository.findById(id).orElseThrow();

        assertEquals("new content", entity.getContent());
    }

    @Test
    void testCreateContent_AlreadyExists() {
        ConceptualModelEntity model = createConceptualModel();

        UUID id = service.createCollectionInstrument(
                CollectionInstrumentMode.PAPI,
                CollectionInstrumentType.BINARY,
                model.getPoguesVersionId(),
                "existing content"
        );

        assertThrows(RuntimeException.class,
                () -> service.createContent(id, "new content"));
    }

    @Test
    void testCreateContent_NotFound() {
        UUID id = UUID.randomUUID();

        assertThrows(IllegalArgumentException.class,
                () -> service.createContent(id, "content"));
    }
}