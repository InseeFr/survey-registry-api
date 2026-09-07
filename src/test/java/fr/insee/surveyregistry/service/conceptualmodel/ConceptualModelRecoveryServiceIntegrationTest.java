package fr.insee.surveyregistry.service.conceptualmodel;

import fr.insee.surveyregistry.dto.conceptualmodel.ConceptualModelMetadataDto;
import fr.insee.surveyregistry.entity.ConceptualModelEntity;
import fr.insee.surveyregistry.exception.ResourceNotFoundException;
import fr.insee.surveyregistry.repository.CollectionInstrumentRepository;
import fr.insee.surveyregistry.repository.ConceptualModelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ConceptualModelRecoveryServiceIntegrationTest {

    @Autowired
    private ConceptualModelRecoveryService service;

    @Autowired
    private CollectionInstrumentRepository collectionInstrumentRepository;

    @Autowired
    private ConceptualModelRepository conceptualModelRepository;

    @BeforeEach
    void cleanDatabase() {
        collectionInstrumentRepository.deleteAll();
        conceptualModelRepository.deleteAll();
    }

    @Test
    void testGetByPoguesVersionId() {
        // Given
        UUID poguesVersionId = UUID.randomUUID();

        ConceptualModelEntity entity = new ConceptualModelEntity();
        entity.setPoguesVersionId(poguesVersionId);
        entity.setPoguesId("mquod4mj");
        entity.setSerieId("s1193");

        conceptualModelRepository.save(entity);

        // When
        ConceptualModelMetadataDto result =
                service.getByPoguesVersionId(poguesVersionId);

        // Then
        assertNotNull(result);
        assertEquals(poguesVersionId, result.poguesVersionId());
        assertEquals("mquod4mj", result.poguesId());
        assertEquals("s1193", result.serieId());
    }

    @Test
    void testGetByPoguesVersionId_NotFound() {
        // Given
        UUID poguesVersionId = UUID.randomUUID();

        // When / Then
        assertThrows(
                ResourceNotFoundException.class,
                () -> service.getByPoguesVersionId(poguesVersionId)
        );
    }

    @Test
    void testGetDDIByPoguesVersionId() {
        // Given
        UUID poguesVersionId = UUID.randomUUID();

        ConceptualModelEntity entity = new ConceptualModelEntity();
        entity.setPoguesVersionId(poguesVersionId);
        entity.setPoguesId("mquod4mj");
        entity.setSerieId("s1193");
        entity.setDdiContent("<DDIInstance>content</DDIInstance>");

        conceptualModelRepository.save(entity);

        // When
        String result =
                service.getDDIByPoguesVersionId(poguesVersionId);

        // Then
        assertNotNull(result);
        assertEquals("<DDIInstance>content</DDIInstance>", result);
    }

    @Test
    void testGetDDIByPoguesVersionId_NotFound() {
        // Given
        UUID poguesVersionId = UUID.randomUUID();

        // When / Then
        assertThrows(
                ResourceNotFoundException.class,
                () -> service.getDDIByPoguesVersionId(poguesVersionId)
        );
    }
}