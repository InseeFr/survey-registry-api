package fr.insee.surveyregistry.service.collectioninstrument;

import fr.insee.surveyregistry.dto.collectioninstrument.CollectionInstrumentLunaticContentDto;
import fr.insee.surveyregistry.dto.collectioninstrument.CollectionInstrumentMetadataDto;
import fr.insee.surveyregistry.entity.CollectionInstrumentEntity;
import fr.insee.surveyregistry.entity.ConceptualModelEntity;
import fr.insee.surveyregistry.enums.CollectionInstrumentMode;
import fr.insee.surveyregistry.exception.ResourceNotFoundException;
import fr.insee.surveyregistry.repository.CollectionInstrumentRepository;
import fr.insee.surveyregistry.repository.ConceptualModelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CollectionInstrumentRecoveryServiceIntegrationTest {

    @Autowired
    private CollectionInstrumentRecoveryService service;

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
    void testGetMetadataById() {
        // Given
        UUID poguesVersionId = UUID.randomUUID();

        ConceptualModelEntity conceptualModelEntity = new ConceptualModelEntity();
        conceptualModelEntity.setPoguesVersionId(poguesVersionId);
        conceptualModelEntity.setPoguesId("mquod4mj");
        conceptualModelEntity.setSerieId("s1193");
        conceptualModelEntity.setDdiContent("<DDIInstance></DDIInstance>");

        conceptualModelRepository.save(conceptualModelEntity);

        UUID collectionInstrumentId = UUID.randomUUID();

        CollectionInstrumentEntity collectionInstrumentEntity = new CollectionInstrumentEntity();
        collectionInstrumentEntity.setCollectionInstrumentId(collectionInstrumentId);
        collectionInstrumentEntity.setConceptualModel(conceptualModelEntity);
        collectionInstrumentEntity.setMode(CollectionInstrumentMode.CAWI);
        collectionInstrumentEntity.setVersion(1);
        collectionInstrumentEntity.setGenerationParameters(Map.of("key", "value"));
        collectionInstrumentEntity.setReleaseDescription("Initial release");
        collectionInstrumentEntity.setReleaseDate(Instant.now());

        // Important : Lunatic content is present,
        // but must not be loaded by the metadata projection.
        collectionInstrumentEntity.setLunaticContent(Map.of("questionnaire", "content"));

        collectionInstrumentRepository.save(collectionInstrumentEntity);

        // When
        CollectionInstrumentMetadataDto result = service.getMetadataById(collectionInstrumentId, null);

        // Then
        assertNotNull(result);

        assertEquals(collectionInstrumentId, result.collectionInstrumentId());
        assertEquals(poguesVersionId, result.poguesVersionId());
        assertEquals(CollectionInstrumentMode.CAWI, result.mode());
        assertEquals(1, result.version());
        assertEquals(Map.of("key", "value"), result.generationParameters());
        assertEquals("Initial release", result.releaseDescription());
        assertNotNull(result.releaseDate());
    }

    @Test
    void testGetMetadataById_NotFound() {
        // Given
        UUID collectionInstrumentId = UUID.randomUUID();

        // When / Then
        assertThrows(
                ResourceNotFoundException.class,
                () -> service.getMetadataById(collectionInstrumentId, null)
        );
    }

    @Test
    void testGetLunaticContentById() {
        // Given
        UUID poguesVersionId = UUID.randomUUID();
        UUID collectionInstrumentId = UUID.randomUUID();

        ConceptualModelEntity conceptualModelEntity = new ConceptualModelEntity();

        conceptualModelEntity.setPoguesVersionId(poguesVersionId);
        conceptualModelEntity.setPoguesId("mquod4mj");
        conceptualModelEntity.setSerieId("s1193");
        conceptualModelEntity.setDdiContent("<DDIInstance></DDIInstance>");

        conceptualModelRepository.save(conceptualModelEntity);

        Map<String, Object> lunaticContent = Map.of("questionnaire", "content");

        CollectionInstrumentEntity collectionInstrumentEntity = new CollectionInstrumentEntity();
        collectionInstrumentEntity.setCollectionInstrumentId(collectionInstrumentId);
        collectionInstrumentEntity.setConceptualModel(conceptualModelEntity);
        collectionInstrumentEntity.setMode(CollectionInstrumentMode.CAWI);
        collectionInstrumentEntity.setVersion(1);
        collectionInstrumentEntity.setGenerationParameters(Map.of("key", "value"));
        collectionInstrumentEntity.setReleaseDescription("Initial release");
        collectionInstrumentEntity.setReleaseDate(Instant.now());
        collectionInstrumentEntity.setLunaticContent(lunaticContent);

        collectionInstrumentRepository.save(collectionInstrumentEntity);

        // When
        CollectionInstrumentLunaticContentDto result =
                service.getLunaticContentById(collectionInstrumentId);

        // Then
        assertNotNull(result);
        assertEquals(lunaticContent, result.lunaticContent());
    }

    @Test
    void testGetLunaticContentById_NotFound() {
        // Given
        UUID collectionInstrumentId = UUID.randomUUID();

        // When / Then
        assertThrows(
                ResourceNotFoundException.class,
                () -> service.getLunaticContentById(collectionInstrumentId)
        );
    }

    @Test
    void testGetMetadataById_WithoutExpand_CodesListsNotLoaded() {
        // Given
        UUID poguesVersionId = UUID.randomUUID();

        ConceptualModelEntity conceptualModelEntity = new ConceptualModelEntity();
        conceptualModelEntity.setPoguesVersionId(poguesVersionId);
        conceptualModelEntity.setPoguesId("mquod4mj");
        conceptualModelEntity.setSerieId("s1193");
        conceptualModelEntity.setDdiContent("<DDIInstance></DDIInstance>");

        conceptualModelRepository.save(conceptualModelEntity);

        UUID collectionInstrumentId = UUID.randomUUID();
        UUID codesListId = UUID.randomUUID();

        CollectionInstrumentEntity collectionInstrumentEntity = new CollectionInstrumentEntity();
        collectionInstrumentEntity.setCollectionInstrumentId(collectionInstrumentId);
        collectionInstrumentEntity.setConceptualModel(conceptualModelEntity);
        collectionInstrumentEntity.setMode(CollectionInstrumentMode.CAWI);
        collectionInstrumentEntity.setVersion(1);
        collectionInstrumentEntity.setGenerationParameters(Map.of("key", "value"));
        collectionInstrumentEntity.setReleaseDescription("Initial release");
        collectionInstrumentEntity.setReleaseDate(Instant.now());
        collectionInstrumentEntity.setLunaticContent(Map.of(
                "suggesters", List.of(
                        Map.of("name", codesListId.toString())
                )
        ));

        collectionInstrumentRepository.save(collectionInstrumentEntity);

        // When
        CollectionInstrumentMetadataDto result = service.getMetadataById(collectionInstrumentId, null);

        // Then
        assertNotNull(result);
        assertNull(result.codesLists());
    }

    @Test
    void testGetDDIById() {
        // Given
        UUID poguesVersionId = UUID.randomUUID();

        ConceptualModelEntity conceptualModelEntity = new ConceptualModelEntity();
        conceptualModelEntity.setPoguesVersionId(poguesVersionId);
        conceptualModelEntity.setPoguesId("mquod4mj");
        conceptualModelEntity.setSerieId("s1193");
        conceptualModelEntity.setDdiContent("<DDIInstance></DDIInstance>");

        conceptualModelRepository.save(conceptualModelEntity);

        UUID collectionInstrumentId = UUID.randomUUID();

        CollectionInstrumentEntity collectionInstrumentEntity = new CollectionInstrumentEntity();
        collectionInstrumentEntity.setCollectionInstrumentId(collectionInstrumentId);
        collectionInstrumentEntity.setConceptualModel(conceptualModelEntity);
        collectionInstrumentEntity.setMode(CollectionInstrumentMode.CAWI);
        collectionInstrumentEntity.setVersion(1);
        collectionInstrumentEntity.setGenerationParameters(Map.of("key", "value"));
        collectionInstrumentEntity.setReleaseDescription("Initial release");
        collectionInstrumentEntity.setReleaseDate(Instant.now());

        collectionInstrumentRepository.save(collectionInstrumentEntity);

        // When
        String ddiResult = service.getDDIById(collectionInstrumentId);

        // Then
        assertNotNull(ddiResult);
        assertEquals("<DDIInstance></DDIInstance>", ddiResult);
    }

    @Test
    void testGetDDIById_NotFound() {
        // Given
        UUID collectionInstrumentId = UUID.randomUUID();

        // When / Then
        assertThrows(
                ResourceNotFoundException.class,
                () -> service.getDDIById(collectionInstrumentId)
        );
    }
}
