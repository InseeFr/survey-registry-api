package fr.insee.surveyregistry.service;

import fr.insee.surveyregistry.dto.CollectionInstrumentMetadataDto;
import fr.insee.surveyregistry.entity.CollectionInstrumentEntity;
import fr.insee.surveyregistry.entity.ConceptualModelEntity;
import fr.insee.surveyregistry.enums.CollectionInstrumentMode;
import fr.insee.surveyregistry.enums.CollectionInstrumentType;
import fr.insee.surveyregistry.repository.CollectionInstrumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CollectionInstrumentRecoveryServiceTest {

    private CollectionInstrumentRepository repository;
    private CollectionInstrumentRecoveryService service;

    @BeforeEach
    void setUp() {
        repository = mock(CollectionInstrumentRepository.class);
        service = new CollectionInstrumentRecoveryService(repository);
    }

    @Test
    void testGetContentById() {
        UUID id = UUID.randomUUID();

        CollectionInstrumentEntity entity = new CollectionInstrumentEntity();
        entity.setContent("content");

        when(repository.findById(id)).thenReturn(Optional.of(entity));

        String result = service.getContentById(id);

        assertEquals("content", result);
    }

    @Test
    void testGetMetadataById() {
        UUID id = UUID.randomUUID();

        ConceptualModelEntity model = new ConceptualModelEntity();
        model.setPoguesVersionId(UUID.randomUUID());

        CollectionInstrumentEntity entity = new CollectionInstrumentEntity();
        entity.setMode(CollectionInstrumentMode.CAWI);
        entity.setType(CollectionInstrumentType.JSON);
        entity.setConceptualModel(model);

        when(repository.findById(id)).thenReturn(Optional.of(entity));

        CollectionInstrumentMetadataDto result = service.getMetadataById(id);

        assertEquals(CollectionInstrumentMode.CAWI, result.mode());
        assertEquals(CollectionInstrumentType.JSON, result.type());
        assertEquals(model.getPoguesVersionId(), result.conceptualModelId());
    }

    @Test
    void testGetContentById_NotFound() {
        UUID id = UUID.randomUUID();

        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> service.getContentById(id));
    }

    @Test
    void testGetMetadataById_NotFound() {
        UUID id = UUID.randomUUID();

        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> service.getMetadataById(id));
    }
}