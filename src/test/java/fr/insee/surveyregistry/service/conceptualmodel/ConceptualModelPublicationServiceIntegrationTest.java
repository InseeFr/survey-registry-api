package fr.insee.surveyregistry.service.conceptualmodel;

import fr.insee.surveyregistry.dto.conceptualmodel.ConceptualModelDto;
import fr.insee.surveyregistry.dto.conceptualmodel.ConceptualModelMetadataDto;
import fr.insee.surveyregistry.entity.ConceptualModelEntity;
import fr.insee.surveyregistry.exception.ResourceAlreadyExistsException;
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
class ConceptualModelPublicationServiceIntegrationTest {

    @Autowired
    private ConceptualModelPublicationService service;

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
    void testCreate() {
        // Given
        UUID poguesVersionId = UUID.randomUUID();

        ConceptualModelMetadataDto dto =
                new ConceptualModelMetadataDto(
                        poguesVersionId,
                        "mquod4mj",
                        "s1193"
                );

        // When
        ConceptualModelDto result = service.create(dto);

        // Then
        assertNotNull(result);

        assertEquals(poguesVersionId, result.poguesVersionId());

        assertNotNull(result.metadata());
        assertEquals(poguesVersionId, result.metadata().poguesVersionId());
        assertEquals("mquod4mj", result.metadata().poguesId());
        assertEquals("s1193", result.metadata().serieId());

        // DDI missing at creation
        assertNull(result.ddiContent());

        ConceptualModelEntity entity =
                conceptualModelRepository.findById(poguesVersionId)
                        .orElseThrow();

        assertEquals(poguesVersionId, entity.getPoguesVersionId());
        assertEquals("mquod4mj", entity.getPoguesId());
        assertEquals("s1193", entity.getSerieId());
        assertNull(entity.getDdiContent());
    }

    @Test
    void testCreate_AlreadyExists() {
        // Given
        UUID poguesVersionId = UUID.randomUUID();

        ConceptualModelEntity entity = new ConceptualModelEntity();
        entity.setPoguesVersionId(poguesVersionId);
        entity.setPoguesId("mquod4mj");
        entity.setSerieId("s1193");

        conceptualModelRepository.save(entity);

        ConceptualModelMetadataDto dto =
                new ConceptualModelMetadataDto(
                        poguesVersionId,
                        "mquod4mj",
                        "s1193"
                );

        // When / Then
        assertThrows(
                ResourceAlreadyExistsException.class,
                () -> service.create(dto)
        );

        assertEquals(1, conceptualModelRepository.count());
    }


    @Test
    void testAddDdiContent() {
        // Given
        UUID poguesVersionId = UUID.randomUUID();

        ConceptualModelEntity entity = new ConceptualModelEntity();
        entity.setPoguesVersionId(poguesVersionId);
        entity.setPoguesId("mquod4mj");
        entity.setSerieId("s1193");

        conceptualModelRepository.save(entity);

        String ddiContent = "<DDIInstance></DDIInstance>";

        // When
        service.addDdiContent(
                poguesVersionId,
                ddiContent
        );

        // Then
        ConceptualModelEntity saved =
                conceptualModelRepository.findById(poguesVersionId)
                        .orElseThrow();

        assertEquals(ddiContent, saved.getDdiContent());
    }


    @Test
    void testAddDdiContent_AlreadyExists() {
        // Given
        UUID poguesVersionId = UUID.randomUUID();

        ConceptualModelEntity entity = new ConceptualModelEntity();
        entity.setPoguesVersionId(poguesVersionId);
        entity.setPoguesId("mquod4mj");
        entity.setSerieId("s1193");
        entity.setDdiContent("<DDIInstance></DDIInstance>");

        conceptualModelRepository.save(entity);

        // When / Then
        assertThrows(
                ResourceAlreadyExistsException.class,
                () -> service.addDdiContent(
                        poguesVersionId,
                        "<DDIInstance>new</DDIInstance>"
                )
        );
    }


    @Test
    void testAddDdiContent_NotFound() {
        // Given
        UUID poguesVersionId = UUID.randomUUID();

        // When / Then
        assertThrows(
                ResourceNotFoundException.class,
                () -> service.addDdiContent(
                        poguesVersionId,
                        "<DDIInstance></DDIInstance>"
                )
        );
    }
}
