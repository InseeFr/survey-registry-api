package fr.insee.surveyregistry.service.collectioninstrument;

import fr.insee.surveyregistry.dto.collectioninstrument.CollectionInstrumentDto;
import fr.insee.surveyregistry.dto.collectioninstrument.CollectionInstrumentLunaticContentDto;
import fr.insee.surveyregistry.dto.collectioninstrument.CollectionInstrumentMetadataDto;
import fr.insee.surveyregistry.entity.CollectionInstrumentEntity;
import fr.insee.surveyregistry.entity.ConceptualModelEntity;
import fr.insee.surveyregistry.enums.CollectionInstrumentMode;
import fr.insee.surveyregistry.exception.InvalidLunaticContentException;
import fr.insee.surveyregistry.exception.InvalidRequestException;
import fr.insee.surveyregistry.exception.ResourceAlreadyExistsException;
import fr.insee.surveyregistry.exception.ResourceNotFoundException;
import fr.insee.surveyregistry.mapper.collectioninstrument.CollectionInstrumentMapper;
import fr.insee.surveyregistry.repository.CollectionInstrumentRepository;
import fr.insee.surveyregistry.repository.ConceptualModelRepository;
import fr.insee.surveyregistry.validation.LunaticContentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class CollectionInstrumentPublicationServiceTest {

    private CollectionInstrumentRepository collectionInstrumentRepository;
    private ConceptualModelRepository conceptualModelRepository;
    private CollectionInstrumentMapper collectionInstrumentMapper;
    private CollectionInstrumentPublicationService service;
    private LunaticContentValidator lunaticContentValidator;

    @BeforeEach
    void setUp() {
        collectionInstrumentRepository = mock(CollectionInstrumentRepository.class);
        conceptualModelRepository = mock(ConceptualModelRepository.class);
        collectionInstrumentMapper = mock(CollectionInstrumentMapper.class);
        lunaticContentValidator = mock(LunaticContentValidator.class);

        service = new CollectionInstrumentPublicationService(
                collectionInstrumentRepository,
                conceptualModelRepository,
                collectionInstrumentMapper,
                lunaticContentValidator
        );
    }

    @Test
    void testCreateCollectionInstrumentMetadataOnly() {
        // Given
        UUID poguesVersionId = UUID.randomUUID();

        CollectionInstrumentMetadataDto metadataDto =
                createMetadataDto(poguesVersionId, null);

        ConceptualModelEntity conceptualModelEntity = new ConceptualModelEntity();
        conceptualModelEntity.setPoguesVersionId(poguesVersionId);
        conceptualModelEntity.setPoguesId("mquod4mj");
        conceptualModelEntity.setDdiContent("<DDIInstance></DDIInstance>");

        CollectionInstrumentEntity collectionInstrumentEntity =
                new CollectionInstrumentEntity();
        collectionInstrumentEntity.setMode(CollectionInstrumentMode.CAWI);
        collectionInstrumentEntity.setGenerationParameters(Map.of());
        collectionInstrumentEntity.setReleaseDescription("Release");

        when(conceptualModelRepository.findById(poguesVersionId))
                .thenReturn(Optional.of(conceptualModelEntity));

        when(collectionInstrumentMapper.toEntity(any(), eq(conceptualModelEntity)))
                .thenReturn(collectionInstrumentEntity);

        when(collectionInstrumentRepository.findMaxVersionByPoguesIdAndMode(
                "mquod4mj",
                CollectionInstrumentMode.CAWI))
                .thenReturn(Optional.empty());

        when(collectionInstrumentRepository.existsByConceptualModel_PoguesIdAndModeAndVersion(
                "mquod4mj",
                CollectionInstrumentMode.CAWI,
                1))
                .thenReturn(false);

        // When
        UUID result = service.createCollectionInstrumentMetadataOnly(metadataDto);

        // Then
        assertNotNull(result);
        assertEquals(1, collectionInstrumentEntity.getVersion());
        assertNotNull(collectionInstrumentEntity.getCollectionInstrumentId());
        assertNull(collectionInstrumentEntity.getReleaseDate());

        verify(conceptualModelRepository).findById(poguesVersionId);
        verify(collectionInstrumentMapper).toEntity(any(), eq(conceptualModelEntity));
        verify(collectionInstrumentRepository).save(collectionInstrumentEntity);
        verify(lunaticContentValidator, never()).validate(any());
    }

    @Test
    void testCreateCollectionInstrument() {
        // Given
        UUID poguesVersionId = UUID.randomUUID();

        CollectionInstrumentMetadataDto metadataDto =
                createMetadataDto(poguesVersionId, null);

        Map<String, Object> lunaticContent = Map.of("questionnaire", "content");

        CollectionInstrumentDto dto =
                new CollectionInstrumentDto(
                        null,
                        metadataDto,
                        new CollectionInstrumentLunaticContentDto(lunaticContent)
                );

        ConceptualModelEntity conceptualModelEntity = new ConceptualModelEntity();
        conceptualModelEntity.setPoguesVersionId(poguesVersionId);
        conceptualModelEntity.setPoguesId("mquod4mj");
        conceptualModelEntity.setDdiContent("<DDIInstance></DDIInstance>");

        CollectionInstrumentEntity collectionInstrumentEntity =
                new CollectionInstrumentEntity();

        when(conceptualModelRepository.findById(poguesVersionId))
                .thenReturn(Optional.of(conceptualModelEntity));

        when(collectionInstrumentMapper.toEntity(dto, conceptualModelEntity))
                .thenReturn(collectionInstrumentEntity);

        when(collectionInstrumentRepository.findMaxVersionByPoguesIdAndMode(
                "mquod4mj",
                CollectionInstrumentMode.CAWI))
                .thenReturn(Optional.of(2));

        when(collectionInstrumentRepository.existsByConceptualModel_PoguesIdAndModeAndVersion(
                "mquod4mj",
                CollectionInstrumentMode.CAWI,
                3))
                .thenReturn(false);

        // When
        UUID result = service.createCollectionInstrument(dto);

        // Then
        assertNotNull(result);
        assertEquals(3, collectionInstrumentEntity.getVersion());
        assertEquals(lunaticContent, collectionInstrumentEntity.getLunaticContent());
        assertNotNull(collectionInstrumentEntity.getReleaseDate());

        verify(lunaticContentValidator).validate(lunaticContent);
        verify(collectionInstrumentRepository).save(collectionInstrumentEntity);
    }

    @Test
    void testCreateCollectionInstrument_InvalidLunaticContent() {
        // Given
        UUID poguesVersionId = UUID.randomUUID();

        CollectionInstrumentMetadataDto metadataDto =
                createMetadataDto(poguesVersionId, null);

        Map<String, Object> lunaticContent =
                Map.of("suggesters", "not-a-uuid");

        CollectionInstrumentDto dto =
                new CollectionInstrumentDto(
                        null,
                        metadataDto,
                        new CollectionInstrumentLunaticContentDto(lunaticContent)
                );

        ConceptualModelEntity conceptualModelEntity = new ConceptualModelEntity();
        conceptualModelEntity.setPoguesVersionId(poguesVersionId);
        conceptualModelEntity.setPoguesId("mquod4mj");
        conceptualModelEntity.setDdiContent("<DDIInstance></DDIInstance>");

        CollectionInstrumentEntity collectionInstrumentEntity =
                new CollectionInstrumentEntity();

        when(conceptualModelRepository.findById(poguesVersionId))
                .thenReturn(Optional.of(conceptualModelEntity));

        when(collectionInstrumentMapper.toEntity(dto, conceptualModelEntity))
                .thenReturn(collectionInstrumentEntity);

        when(collectionInstrumentRepository.findMaxVersionByPoguesIdAndMode(
                "mquod4mj",
                CollectionInstrumentMode.CAWI))
                .thenReturn(Optional.empty());

        when(collectionInstrumentRepository.existsByConceptualModel_PoguesIdAndModeAndVersion(
                "mquod4mj",
                CollectionInstrumentMode.CAWI,
                1))
                .thenReturn(false);

        doThrow(new InvalidLunaticContentException("Lunatic content field 'suggesters' must be an array"))
                .when(lunaticContentValidator).validate(lunaticContent);

        // When / Then
        assertThrows(
                InvalidLunaticContentException.class,
                () -> service.createCollectionInstrument(dto)
        );

        verify(collectionInstrumentRepository, never()).save(any());
    }

    @Test
    void testCreateCollectionInstrument_ConceptualModelNotFound() {
        // Given
        UUID poguesVersionId = UUID.randomUUID();

        CollectionInstrumentDto dto =
                new CollectionInstrumentDto(
                        null,
                        new CollectionInstrumentMetadataDto(
                                null,
                                poguesVersionId,
                                CollectionInstrumentMode.CAWI,
                                null,
                                Map.of(),
                                "Release",
                                null,
                                null
                        ),
                        null
                );

        when(conceptualModelRepository.findById(poguesVersionId))
                .thenReturn(Optional.empty());

        // When / Then
        assertThrows(
                InvalidRequestException.class,
                () -> service.createCollectionInstrument(dto)
        );

        verify(collectionInstrumentRepository, never()).save(any());
    }

    @Test
    void testCreateCollectionInstrument_DdiMissing() {
        // Given
        UUID poguesVersionId = UUID.randomUUID();

        ConceptualModelEntity conceptualModelEntity = new ConceptualModelEntity();
        conceptualModelEntity.setPoguesVersionId(poguesVersionId);

        CollectionInstrumentDto dto =
                new CollectionInstrumentDto(
                        null,
                        new CollectionInstrumentMetadataDto(
                                null,
                                poguesVersionId,
                                CollectionInstrumentMode.CAWI,
                                null,
                                Map.of(),
                                "Release",
                                null,
                                null
                        ),
                        null
                );

        when(conceptualModelRepository.findById(poguesVersionId))
                .thenReturn(Optional.of(conceptualModelEntity));

        // When / Then
        assertThrows(
                InvalidRequestException.class,
                () -> service.createCollectionInstrument(dto)
        );

        verify(collectionInstrumentRepository, never()).save(any());
    }

    @Test
    void testCreateCollectionInstrument_AlreadyExists() {
        // Given
        UUID poguesVersionId = UUID.randomUUID();

        CollectionInstrumentMetadataDto metadataDto =
                createMetadataDto(poguesVersionId, 1);

        CollectionInstrumentDto dto =
                new CollectionInstrumentDto(
                        null,
                        metadataDto,
                        null
                );

        ConceptualModelEntity conceptualModelEntity = new ConceptualModelEntity();
        conceptualModelEntity.setPoguesVersionId(poguesVersionId);
        conceptualModelEntity.setPoguesId("mquod4mj");
        conceptualModelEntity.setDdiContent("<DDIInstance></DDIInstance>");

        CollectionInstrumentEntity collectionInstrumentEntity =
                new CollectionInstrumentEntity();
        collectionInstrumentEntity.setVersion(1);

        when(conceptualModelRepository.findById(poguesVersionId))
                .thenReturn(Optional.of(conceptualModelEntity));

        when(collectionInstrumentMapper.toEntity(dto, conceptualModelEntity))
                .thenReturn(collectionInstrumentEntity);

        when(collectionInstrumentRepository.existsByConceptualModel_PoguesIdAndModeAndVersion(
                "mquod4mj",
                CollectionInstrumentMode.CAWI,
                1))
                .thenReturn(true);

        // When / Then
        assertThrows(
                ResourceAlreadyExistsException.class,
                () -> service.createCollectionInstrument(dto)
        );
        verify(
                collectionInstrumentRepository,
                never()
        ).existsByConceptualModel_PoguesVersionIdAndMode(
                poguesVersionId,
                CollectionInstrumentMode.CAWI
        );

        verify(collectionInstrumentRepository, never()).save(any());
        verify(lunaticContentValidator, never()).validate(any());
    }

    @Test
    void testCreateCollectionInstrument_AlreadyExistsForPoguesVersionAndMode() {
        // Given
        UUID poguesVersionId = UUID.randomUUID();

        CollectionInstrumentMetadataDto metadataDto =
                createMetadataDto(poguesVersionId, 1);

        CollectionInstrumentDto dto =
                new CollectionInstrumentDto(
                        null,
                        metadataDto,
                        null
                );

        ConceptualModelEntity conceptualModelEntity = new ConceptualModelEntity();
        conceptualModelEntity.setPoguesVersionId(poguesVersionId);
        conceptualModelEntity.setPoguesId("mquod4mj");
        conceptualModelEntity.setDdiContent("<DDIInstance></DDIInstance>");

        CollectionInstrumentEntity collectionInstrumentEntity =
                new CollectionInstrumentEntity();
        collectionInstrumentEntity.setVersion(1);

        when(conceptualModelRepository.findById(poguesVersionId))
                .thenReturn(Optional.of(conceptualModelEntity));

        when(collectionInstrumentMapper.toEntity(dto, conceptualModelEntity))
                .thenReturn(collectionInstrumentEntity);

        when(collectionInstrumentRepository.existsByConceptualModel_PoguesIdAndModeAndVersion(
                "mquod4mj",
                CollectionInstrumentMode.CAWI,
                1
        )).thenReturn(false);

        when(collectionInstrumentRepository.existsByConceptualModel_PoguesVersionIdAndMode(
                poguesVersionId,
                CollectionInstrumentMode.CAWI
        )).thenReturn(true);

        // When / Then
        ResourceAlreadyExistsException exception = assertThrows(
                ResourceAlreadyExistsException.class,
                () -> service.createCollectionInstrument(dto)
        );

        assertEquals(
                "Collection instrument already exists for poguesVersionId="
                        + poguesVersionId
                        + ", mode=CAWI",
                exception.getMessage()
        );

        verify(collectionInstrumentRepository)
                .existsByConceptualModel_PoguesVersionIdAndMode(
                        poguesVersionId,
                        CollectionInstrumentMode.CAWI
                );

        verify(collectionInstrumentRepository, never()).save(any());
        verify(lunaticContentValidator, never()).validate(any());
    }

    @Test
    void testAddLunaticContent() {
        // Given
        UUID collectionInstrumentId = UUID.randomUUID();

        Map<String, Object> lunaticContent =
                Map.of("questionnaire", "content");

        CollectionInstrumentLunaticContentDto lunaticContentDto =
                new CollectionInstrumentLunaticContentDto(lunaticContent);

        CollectionInstrumentEntity collectionInstrumentEntity =
                new CollectionInstrumentEntity();

        collectionInstrumentEntity.setCollectionInstrumentId(
                collectionInstrumentId);

        collectionInstrumentEntity.setLunaticContent(null);
        collectionInstrumentEntity.setReleaseDate(null);

        when(collectionInstrumentRepository.findById(collectionInstrumentId))
                .thenReturn(Optional.of(collectionInstrumentEntity));

        // When
        service.addLunaticContent(collectionInstrumentId, lunaticContentDto);

        // Then
        assertEquals(
                lunaticContent,
                collectionInstrumentEntity.getLunaticContent()
        );
        assertNotNull(collectionInstrumentEntity.getReleaseDate());

        verify(collectionInstrumentRepository).findById(collectionInstrumentId);
        verify(lunaticContentValidator).validate(lunaticContent);
        verify(collectionInstrumentRepository).save(collectionInstrumentEntity);
    }

    @Test
    void testAddLunaticContent_InvalidContent() {
        // Given
        UUID collectionInstrumentId = UUID.randomUUID();

        Map<String, Object> lunaticContent =
                Map.of("suggesters", "not-a-uuid");

        CollectionInstrumentLunaticContentDto lunaticContentDto =
                new CollectionInstrumentLunaticContentDto(lunaticContent);

        CollectionInstrumentEntity collectionInstrumentEntity =
                new CollectionInstrumentEntity();

        collectionInstrumentEntity.setCollectionInstrumentId(collectionInstrumentId);
        collectionInstrumentEntity.setLunaticContent(null);

        when(collectionInstrumentRepository.findById(collectionInstrumentId))
                .thenReturn(Optional.of(collectionInstrumentEntity));

        doThrow(new InvalidLunaticContentException("Lunatic content field 'suggesters' must be an array"))
                .when(lunaticContentValidator).validate(lunaticContent);

        // When / Then
        assertThrows(
                InvalidLunaticContentException.class,
                () -> service.addLunaticContent(collectionInstrumentId, lunaticContentDto)
        );

        verify(collectionInstrumentRepository, never()).save(any());
    }

    @Test
    void testAddLunaticContent_NotFound() {
        // Given
        UUID collectionInstrumentId = UUID.randomUUID();

        Map<String, Object> lunaticContent =
                Map.of("questionnaire", "content");

        CollectionInstrumentLunaticContentDto lunaticContentDto =
                new CollectionInstrumentLunaticContentDto(lunaticContent);

        when(collectionInstrumentRepository.findById(collectionInstrumentId))
                .thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class,
                () -> service.addLunaticContent(
                        collectionInstrumentId,
                        lunaticContentDto
                )
        );

        verify(collectionInstrumentRepository).findById(collectionInstrumentId);
        verify(collectionInstrumentRepository, never()).save(any());
        verify(lunaticContentValidator, never()).validate(any());
    }

    @Test
    void testAddLunaticContent_AlreadyExists() {
        // Given
        UUID collectionInstrumentId = UUID.randomUUID();

        Map<String, Object> existingLunaticContent =
                Map.of("questionnaire", "existing");

        Map<String, Object> newLunaticContent =
                Map.of("questionnaire", "new");

        CollectionInstrumentLunaticContentDto lunaticContentDto =
                new CollectionInstrumentLunaticContentDto(newLunaticContent);

        CollectionInstrumentEntity collectionInstrumentEntity =
                new CollectionInstrumentEntity();

        collectionInstrumentEntity.setCollectionInstrumentId(collectionInstrumentId);
        collectionInstrumentEntity.setLunaticContent(existingLunaticContent);

        when(collectionInstrumentRepository.findById(collectionInstrumentId))
                .thenReturn(Optional.of(collectionInstrumentEntity));

        // When / Then
        assertThrows(
                ResourceAlreadyExistsException.class,
                () -> service.addLunaticContent(
                        collectionInstrumentId,
                        lunaticContentDto
                )
        );

        verify(collectionInstrumentRepository).findById(collectionInstrumentId);
        verify(collectionInstrumentRepository, never()).save(any());
        verify(lunaticContentValidator, never()).validate(any());
    }

    private CollectionInstrumentMetadataDto createMetadataDto(
            UUID poguesVersionId,
            Integer version) {
        return new CollectionInstrumentMetadataDto(
                null,
                poguesVersionId,
                CollectionInstrumentMode.CAWI,
                version,
                Map.of(),
                "Release",
                null,
                null
        );
    }
}