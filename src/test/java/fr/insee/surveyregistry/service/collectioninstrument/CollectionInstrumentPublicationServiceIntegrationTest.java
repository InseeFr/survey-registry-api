package fr.insee.surveyregistry.service.collectioninstrument;

import fr.insee.surveyregistry.dto.collectioninstrument.CollectionInstrumentDto;
import fr.insee.surveyregistry.dto.collectioninstrument.CollectionInstrumentLunaticContentDto;
import fr.insee.surveyregistry.dto.collectioninstrument.CollectionInstrumentMetadataDto;
import fr.insee.surveyregistry.entity.CollectionInstrumentEntity;
import fr.insee.surveyregistry.entity.ConceptualModelEntity;
import fr.insee.surveyregistry.enums.CollectionInstrumentMode;
import fr.insee.surveyregistry.exception.InvalidRequestException;
import fr.insee.surveyregistry.exception.ResourceAlreadyExistsException;
import fr.insee.surveyregistry.exception.ResourceNotFoundException;
import fr.insee.surveyregistry.repository.CollectionInstrumentRepository;
import fr.insee.surveyregistry.repository.ConceptualModelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CollectionInstrumentPublicationServiceIntegrationTest {

    @Autowired
    private CollectionInstrumentPublicationService service;

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
    void testCreateCollectionInstrumentMetadataOnly() {
        // Given
        UUID poguesVersionId = UUID.randomUUID();

        ConceptualModelEntity conceptualModelEntity = new ConceptualModelEntity();
        conceptualModelEntity.setPoguesVersionId(poguesVersionId);
        conceptualModelEntity.setPoguesId("mquod4mj");
        conceptualModelEntity.setSerieId("s1193");
        conceptualModelEntity.setDdiContent("<DDIInstance></DDIInstance>");

        conceptualModelRepository.save(conceptualModelEntity);

        CollectionInstrumentMetadataDto metadataDto =
                new CollectionInstrumentMetadataDto(
                        null,
                        poguesVersionId,
                        CollectionInstrumentMode.CAWI,
                        null,
                        Map.of("key", "value"),
                        "Initial release",
                        null,
                        null
                );

        // When
        UUID collectionInstrumentId =
                service.createCollectionInstrumentMetadataOnly(metadataDto);

        // Then
        assertNotNull(collectionInstrumentId);

        CollectionInstrumentEntity collectionInstrumentEntity =
                collectionInstrumentRepository.findById(collectionInstrumentId)
                        .orElseThrow();

        assertEquals(collectionInstrumentId,
                collectionInstrumentEntity.getCollectionInstrumentId());
        assertEquals(poguesVersionId,
                collectionInstrumentEntity.getConceptualModel().getPoguesVersionId());
        assertEquals(CollectionInstrumentMode.CAWI,
                collectionInstrumentEntity.getMode());
        assertEquals(1,
                collectionInstrumentEntity.getVersion());
        assertEquals(Map.of("key", "value"),
                collectionInstrumentEntity.getGenerationParameters());
        assertEquals("Initial release",
                collectionInstrumentEntity.getReleaseDescription());

        assertNull(collectionInstrumentEntity.getLunaticContent());
        assertNull(collectionInstrumentEntity.getReleaseDate());
    }

    @Test
    void testCreateCollectionInstrument() {
        // Given
        UUID poguesVersionId = UUID.randomUUID();

        ConceptualModelEntity conceptualModelEntity = new ConceptualModelEntity();
        conceptualModelEntity.setPoguesVersionId(poguesVersionId);
        conceptualModelEntity.setPoguesId("mquod4mj");
        conceptualModelEntity.setSerieId("s1193");
        conceptualModelEntity.setDdiContent("<DDIInstance></DDIInstance>");

        conceptualModelRepository.save(conceptualModelEntity);

        Map<String, Object> lunaticContent = Map.of("questionnaire", "content");

        CollectionInstrumentDto dto = createDto(
                poguesVersionId,
                new CollectionInstrumentLunaticContentDto(lunaticContent));

        // When
        UUID collectionInstrumentId = service.createCollectionInstrument(dto);

        // Then
        CollectionInstrumentEntity collectionInstrumentEntity =
                collectionInstrumentRepository.findById(collectionInstrumentId)
                        .orElseThrow();

        assertEquals(CollectionInstrumentMode.CAWI, collectionInstrumentEntity.getMode());
        assertEquals(1, collectionInstrumentEntity.getVersion());
        assertEquals(lunaticContent, collectionInstrumentEntity.getLunaticContent());
        assertNotNull(collectionInstrumentEntity.getReleaseDate());
    }

    @Test
    void testCreateCollectionInstrument_ConceptualModelNotFound() {
        // Given
        UUID unknownPoguesVersionId = UUID.randomUUID();

        CollectionInstrumentDto dto = createDto(
                unknownPoguesVersionId,
                null);

        // When / Then
        assertThrows(InvalidRequestException.class,
                () -> service.createCollectionInstrument(dto)
        );
    }

    @Test
    void testCreateCollectionInstrument_ConceptualModelWithoutDdi() {
        // Given
        UUID poguesVersionId = UUID.randomUUID();

        ConceptualModelEntity conceptualModelEntity = new ConceptualModelEntity();
        conceptualModelEntity.setPoguesVersionId(poguesVersionId);
        conceptualModelEntity.setPoguesId("mquod4mj");
        conceptualModelEntity.setSerieId("s1193");

        conceptualModelRepository.save(conceptualModelEntity);

        CollectionInstrumentDto dto = createDto(poguesVersionId, null);

        // When / Then
        assertThrows(InvalidRequestException.class,
                () -> service.createCollectionInstrument(dto)
        );
    }

    @Test
    void testCreateCollectionInstrumentMetadataOnly_VersionIsGeneratedByPoguesIdAndMode() {
        // Given
        UUID firstPoguesVersionId = UUID.randomUUID();
        UUID secondPoguesVersionId = UUID.randomUUID();

        ConceptualModelEntity firstConceptualModelEntity = new ConceptualModelEntity();
        firstConceptualModelEntity.setPoguesVersionId(firstPoguesVersionId);
        firstConceptualModelEntity.setPoguesId("mquod4mj");
        firstConceptualModelEntity.setSerieId("s1193");
        firstConceptualModelEntity.setDdiContent("<DDIInstance1></DDIInstance1>");

        ConceptualModelEntity secondConceptualModelEntity = new ConceptualModelEntity();
        secondConceptualModelEntity.setPoguesVersionId(secondPoguesVersionId);
        secondConceptualModelEntity.setPoguesId("mquod4mj");
        secondConceptualModelEntity.setSerieId("s1193");
        secondConceptualModelEntity.setDdiContent("<DDIInstance2></DDIInstance2>");

        conceptualModelRepository.save(firstConceptualModelEntity);
        conceptualModelRepository.save(secondConceptualModelEntity);

        CollectionInstrumentMetadataDto firstMetadataDto =
                new CollectionInstrumentMetadataDto(
                        null,
                        firstPoguesVersionId,
                        CollectionInstrumentMode.CAWI,
                        null,
                        Map.of(),
                        "Initial release 1",
                        null,
                        null
                );

        CollectionInstrumentMetadataDto secondMetadataDto =
                new CollectionInstrumentMetadataDto(
                        null,
                        secondPoguesVersionId,
                        CollectionInstrumentMode.CAWI,
                        null,
                        Map.of(),
                        "Initial release 2",
                        null,
                        null
                );

        // When
        UUID firstCollectionInstrumentId =
                service.createCollectionInstrumentMetadataOnly(firstMetadataDto);

        UUID secondCollectionInstrumentId =
                service.createCollectionInstrumentMetadataOnly(secondMetadataDto);

        // Then
        CollectionInstrumentEntity firstCollectionInstrumentEntity =
                collectionInstrumentRepository.findById(firstCollectionInstrumentId)
                        .orElseThrow();

        CollectionInstrumentEntity secondCollectionInstrumentEntity =
                collectionInstrumentRepository.findById(secondCollectionInstrumentId)
                        .orElseThrow();


        assertEquals(firstPoguesVersionId,
                firstCollectionInstrumentEntity.getConceptualModel().getPoguesVersionId());

        assertEquals(secondPoguesVersionId,
                secondCollectionInstrumentEntity.getConceptualModel().getPoguesVersionId());


        assertEquals(CollectionInstrumentMode.CAWI,
                firstCollectionInstrumentEntity.getMode());

        assertEquals(CollectionInstrumentMode.CAWI,
                secondCollectionInstrumentEntity.getMode());


        assertEquals(1, firstCollectionInstrumentEntity.getVersion());
        assertEquals(2, secondCollectionInstrumentEntity.getVersion());
    }

    @Test
    void testAddLunaticContent() {
        // Given
        UUID poguesVersionId = UUID.randomUUID();

        ConceptualModelEntity conceptualModelEntity = new ConceptualModelEntity();
        conceptualModelEntity.setPoguesVersionId(poguesVersionId);
        conceptualModelEntity.setPoguesId("mquod4mj");
        conceptualModelEntity.setSerieId("s1193");
        conceptualModelEntity.setDdiContent("<DDIInstance></DDIInstance>");

        conceptualModelRepository.save(conceptualModelEntity);

        CollectionInstrumentMetadataDto metadataDto =
                new CollectionInstrumentMetadataDto(
                        null,
                        poguesVersionId,
                        CollectionInstrumentMode.CAWI,
                        null,
                        Map.of(),
                        "Initial release",
                        null,
                        null
                );

        UUID collectionInstrumentId =
                service.createCollectionInstrumentMetadataOnly(metadataDto);

        Map<String, Object> lunaticContent =
                Map.of("questionnaire", "content");

        CollectionInstrumentLunaticContentDto lunaticContentDto =
                new CollectionInstrumentLunaticContentDto(lunaticContent);

        // When
        service.addLunaticContent(collectionInstrumentId, lunaticContentDto);

        // Then
        CollectionInstrumentEntity collectionInstrumentEntity =
                collectionInstrumentRepository.findById(collectionInstrumentId)
                        .orElseThrow();

        assertEquals(lunaticContent, collectionInstrumentEntity.getLunaticContent());
        assertNotNull(collectionInstrumentEntity.getReleaseDate());
    }

    @Test
    void testAddLunaticContent_NotFound() {
        // Given
        UUID collectionInstrumentId = UUID.randomUUID();

        Map<String, Object> lunaticContent =
                Map.of("questionnaire", "content");

        CollectionInstrumentLunaticContentDto lunaticContentDto =
                new CollectionInstrumentLunaticContentDto(lunaticContent);

        // When / Then
        assertThrows(ResourceNotFoundException.class,
                () -> service.addLunaticContent(
                        collectionInstrumentId,
                        lunaticContentDto
                )
        );
    }

    @Test
    void testAddLunaticContent_AlreadyExists() {
        // Given
        UUID poguesVersionId = UUID.randomUUID();

        ConceptualModelEntity conceptualModelEntity = new ConceptualModelEntity();
        conceptualModelEntity.setPoguesVersionId(poguesVersionId);
        conceptualModelEntity.setPoguesId("mquod4mj");
        conceptualModelEntity.setSerieId("s1193");
        conceptualModelEntity.setDdiContent("<DDIInstance></DDIInstance>");

        conceptualModelRepository.save(conceptualModelEntity);

        Map<String, Object> existingLunaticContent =
                Map.of("questionnaire", "existing");

        CollectionInstrumentDto dto = createDto(
                poguesVersionId,
                new CollectionInstrumentLunaticContentDto(existingLunaticContent));

        UUID collectionInstrumentId = service.createCollectionInstrument(dto);

        Map<String, Object> newLunaticContent = Map.of("questionnaire", "new");

        CollectionInstrumentLunaticContentDto newLunaticContentDto =
                new CollectionInstrumentLunaticContentDto(newLunaticContent);

        // When / Then
        assertThrows(ResourceAlreadyExistsException.class,
                () -> service.addLunaticContent(
                        collectionInstrumentId,
                        newLunaticContentDto
                )
        );
    }

    private CollectionInstrumentDto createDto(
            UUID poguesVersionId,
            CollectionInstrumentLunaticContentDto lunaticContentDto) {
        return new CollectionInstrumentDto(
                null,
                new CollectionInstrumentMetadataDto(
                        null,
                        poguesVersionId,
                        CollectionInstrumentMode.CAWI,
                        null,
                        Map.of(),
                        "Initial release",
                        null,
                        null
                ),
                lunaticContentDto
        );
    }
}
