package fr.insee.surveyregistry.mapper.collectioninstrument;

import fr.insee.surveyregistry.dto.collectioninstrument.CollectionInstrumentDto;
import fr.insee.surveyregistry.dto.collectioninstrument.CollectionInstrumentMetadataDto;
import fr.insee.surveyregistry.entity.CollectionInstrumentEntity;
import fr.insee.surveyregistry.entity.ConceptualModelEntity;
import fr.insee.surveyregistry.enums.CollectionInstrumentMode;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CollectionInstrumentMapperTest {

    private final CollectionInstrumentMapper collectionInstrumentMapper =
            new CollectionInstrumentMapper();

    @Test
    void testToDto_WithValidEntityWithoutLunaticContent() {
        // Given
        UUID collectionInstrumentId = UUID.randomUUID();
        UUID poguesVersionId = UUID.randomUUID();

        ConceptualModelEntity conceptualModelEntity =
                createConceptualModelEntity(poguesVersionId);

        CollectionInstrumentEntity collectionInstrumentEntity =
                createCollectionInstrumentEntity(
                        collectionInstrumentId,
                        conceptualModelEntity,
                        1,
                        Map.of("key", "value"),
                        "Initial release",
                        null
                );

        // When
        CollectionInstrumentDto dto =
                collectionInstrumentMapper.toDto(collectionInstrumentEntity);

        // Then
        assertNotNull(dto);
        assertEquals(collectionInstrumentId, dto.collectionInstrumentId());
        assertNotNull(dto.metadata());
        assertEquals(collectionInstrumentId, dto.metadata().collectionInstrumentId());
        assertEquals(poguesVersionId, dto.metadata().poguesVersionId());
        assertEquals(CollectionInstrumentMode.CAWI, dto.metadata().mode());
        assertEquals(1, dto.metadata().version());
        assertEquals(Map.of("key", "value"), dto.metadata().generationParameters());
        assertEquals("Initial release", dto.metadata().releaseDescription());
        assertNull(dto.lunaticContent());
    }

    @Test
    void testToDto_WithValidEntityWithLunaticContent() {
        // Given
        UUID collectionInstrumentId = UUID.randomUUID();
        UUID poguesVersionId = UUID.randomUUID();

        ConceptualModelEntity conceptualModelEntity =
                createConceptualModelEntity(poguesVersionId);

        Map<String, Object> lunaticContent = Map.of("questionnaire", "content");

        CollectionInstrumentEntity collectionInstrumentEntity =
                createCollectionInstrumentEntity(
                        collectionInstrumentId,
                        conceptualModelEntity,
                        2,
                        Map.of(),
                        "Release with Lunatic",
                        lunaticContent
                );

        // When
        CollectionInstrumentDto dto = collectionInstrumentMapper.toDto(collectionInstrumentEntity);

        // Then
        assertNotNull(dto);
        assertNotNull(dto.lunaticContent());
        assertEquals(lunaticContent, dto.lunaticContent().lunaticContent());
        assertEquals(CollectionInstrumentMode.CAWI, dto.metadata().mode());
        assertEquals(2, dto.metadata().version());
    }

    @Test
    void testToDto_WithNullEntity() {
        // When
        CollectionInstrumentDto dto = collectionInstrumentMapper.toDto(null);

        // Then
        assertNull(dto);
    }

    @Test
    void testToEntity_WithValidDto() {
        // Given
        UUID collectionInstrumentId = UUID.randomUUID();
        UUID poguesVersionId = UUID.randomUUID();

        ConceptualModelEntity conceptualModelEntity =
                createConceptualModelEntity(poguesVersionId);

        CollectionInstrumentMetadataDto collectionInstrumentMetadataDto =
                createMetadataDto(collectionInstrumentId, poguesVersionId);

        CollectionInstrumentDto dto =
                new CollectionInstrumentDto(
                        collectionInstrumentId,
                        collectionInstrumentMetadataDto,
                        null
                );

        // When
        CollectionInstrumentEntity collectionInstrumentEntity =
                collectionInstrumentMapper.toEntity(dto, conceptualModelEntity);

        // Then
        assertNotNull(collectionInstrumentEntity);
        assertEquals(collectionInstrumentId, collectionInstrumentEntity.getCollectionInstrumentId());
        assertEquals(conceptualModelEntity, collectionInstrumentEntity.getConceptualModel());
        assertEquals(CollectionInstrumentMode.CAWI, collectionInstrumentEntity.getMode());
        assertEquals(1, collectionInstrumentEntity.getVersion());
        assertEquals(
                Map.of("generation", "parameter"),
                collectionInstrumentEntity.getGenerationParameters()
        );
        assertEquals(
                "Initial release",
                collectionInstrumentEntity.getReleaseDescription()
        );
    }

    @Test
    void testToEntity_WithNullDto() {
        // When
        CollectionInstrumentEntity collectionInstrumentEntity =
                collectionInstrumentMapper.toEntity(
                        null,
                        new ConceptualModelEntity()
                );

        // Then
        assertNull(collectionInstrumentEntity);
    }

    private ConceptualModelEntity createConceptualModelEntity(UUID poguesVersionId) {
        ConceptualModelEntity conceptualModelEntity = new ConceptualModelEntity();

        conceptualModelEntity.setPoguesVersionId(poguesVersionId);

        return conceptualModelEntity;
    }

    private CollectionInstrumentEntity createCollectionInstrumentEntity(
            UUID collectionInstrumentId,
            ConceptualModelEntity conceptualModelEntity,
            Integer version,
            Map<String, Object> generationParameters,
            String releaseDescription,
            Map<String, Object> lunaticContent
    ) {
        CollectionInstrumentEntity entity = new CollectionInstrumentEntity();

        entity.setCollectionInstrumentId(collectionInstrumentId);
        entity.setConceptualModel(conceptualModelEntity);
        entity.setMode(CollectionInstrumentMode.CAWI);
        entity.setVersion(version);
        entity.setGenerationParameters(generationParameters);
        entity.setReleaseDescription(releaseDescription);
        entity.setLunaticContent(lunaticContent);

        return entity;
    }

    private CollectionInstrumentMetadataDto createMetadataDto(
            UUID collectionInstrumentId, UUID poguesVersionId) {
        return new CollectionInstrumentMetadataDto(
                collectionInstrumentId,
                poguesVersionId,
                CollectionInstrumentMode.CAWI,
                1,
                Map.of("generation", "parameter"),
                "Initial release",
                null,
                null
        );
    }
}
