package fr.insee.surveyregistry.service;

import fr.insee.surveyregistry.dto.CollectionInstrumentMetadataDto;
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
class CollectionInstrumentRecoveryServiceIntegrationTest {

    @Autowired
    private CollectionInstrumentRecoveryService service;

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

    private CollectionInstrumentEntity createInstrument(ConceptualModelEntity model) {
        CollectionInstrumentEntity entity = new CollectionInstrumentEntity();
        entity.setCollectionInstrumentId(UUID.randomUUID());
        entity.setConceptualModel(model);
        entity.setMode(CollectionInstrumentMode.CAWI);
        entity.setType(CollectionInstrumentType.JSON);
        entity.setContent("initial content");
        return repository.save(entity);
    }

    @Test
    void testGetContentById() {
        ConceptualModelEntity model = createConceptualModel();
        CollectionInstrumentEntity entity = createInstrument(model);

        String content = service.getContentById(entity.getCollectionInstrumentId());

        assertEquals("initial content", content);
    }

    @Test
    void testGetMetadataById() {
        ConceptualModelEntity model = createConceptualModel();
        CollectionInstrumentEntity entity = createInstrument(model);

        CollectionInstrumentMetadataDto metadata =
                service.getMetadataById(entity.getCollectionInstrumentId());

        assertEquals(CollectionInstrumentMode.CAWI, metadata.mode());
        assertEquals(CollectionInstrumentType.JSON, metadata.type());
        assertEquals(model.getPoguesVersionId(), metadata.conceptualModelId());
    }

    @Test
    void testGetContentById_NotFound() {
        UUID id = UUID.randomUUID();

        assertThrows(IllegalArgumentException.class,
                () -> service.getContentById(id));
    }
}