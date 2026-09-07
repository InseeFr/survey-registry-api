package fr.insee.surveyregistry.mapper.collectioninstrument;

import fr.insee.surveyregistry.dto.collectioninstrument.CollectionInstrumentMetadataDto;
import fr.insee.surveyregistry.enums.CollectionInstrumentMode;
import fr.insee.surveyregistry.repository.CollectionInstrumentRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CollectionInstrumentMetadataMapperTest {

    private final CollectionInstrumentMetadataMapper collectionInstrumentMetadataMapper =
            new CollectionInstrumentMetadataMapper();

    @Test
    void testToDto_WithValidProjection() {
        // Given
        UUID collectionInstrumentId = UUID.randomUUID();
        UUID poguesVersionId = UUID.randomUUID();

        Map<String, Object> generationParameters = Map.of("generation", "parameter");

        Instant releaseDate = Instant.now();

        CollectionInstrumentRepository.MetadataProjection projection =
                Mockito.mock(CollectionInstrumentRepository.MetadataProjection.class);

        CollectionInstrumentRepository.ConceptualModelProjection conceptualModelProjection =
                Mockito.mock(CollectionInstrumentRepository.ConceptualModelProjection.class);

        Mockito.when(projection.getCollectionInstrumentId()).thenReturn(collectionInstrumentId);
        Mockito.when(projection.getConceptualModel()).thenReturn(conceptualModelProjection);
        Mockito.when(conceptualModelProjection.getPoguesVersionId()).thenReturn(poguesVersionId);
        Mockito.when(projection.getMode()).thenReturn(CollectionInstrumentMode.CAWI);
        Mockito.when(projection.getVersion()).thenReturn(1);
        Mockito.when(projection.getGenerationParameters()).thenReturn(generationParameters);
        Mockito.when(projection.getReleaseDescription()).thenReturn("Initial release");
        Mockito.when(projection.getReleaseDate()).thenReturn(releaseDate);

        // When
        CollectionInstrumentMetadataDto dto = collectionInstrumentMetadataMapper.toDto(projection);

        // Then
        assertNotNull(dto);

        assertEquals(collectionInstrumentId, dto.collectionInstrumentId());
        assertEquals(poguesVersionId, dto.poguesVersionId());
        assertEquals(CollectionInstrumentMode.CAWI, dto.mode());
        assertEquals(1, dto.version());
        assertEquals(generationParameters, dto.generationParameters());
        assertEquals("Initial release", dto.releaseDescription());
        assertEquals(releaseDate, dto.releaseDate());

        Mockito.verify(projection).getCollectionInstrumentId();
        Mockito.verify(projection).getConceptualModel();
        Mockito.verify(conceptualModelProjection).getPoguesVersionId();
        Mockito.verify(projection).getMode();
        Mockito.verify(projection).getVersion();
        Mockito.verify(projection).getGenerationParameters();
        Mockito.verify(projection).getReleaseDescription();
        Mockito.verify(projection).getReleaseDate();
    }

    @Test
    void testToDto_WithNullProjection() {
        // When
        CollectionInstrumentMetadataDto dto = collectionInstrumentMetadataMapper.toDto(null);

        // Then
        assertNull(dto);
    }
}
