package fr.insee.surveyregistry.service;

import fr.insee.surveyregistry.dto.CollectionInstrumentMetadataDto;
import fr.insee.surveyregistry.entity.CollectionInstrumentEntity;
import fr.insee.surveyregistry.repository.CollectionInstrumentRepository;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@NullMarked
@Transactional(readOnly = true)
public class CollectionInstrumentRecoveryService {

    private static final String NOT_FOUND_MESSAGE = "CollectionInstrument not found: ";

    private final CollectionInstrumentRepository collectionInstrumentRepository;

    public CollectionInstrumentRecoveryService(CollectionInstrumentRepository collectionInstrumentRepository) {
        this.collectionInstrumentRepository = collectionInstrumentRepository;
    }

    /**
     * Retrieves metadata of a collection instrument.
     * Metadata includes mode, type and conceptual model identifier (poguesVersionId).
     *
     * @param collectionInstrumentId the unique identifier of the collection instrument
     * @return a DTO containing the metadata of the instrument
     * @throws IllegalArgumentException if no instrument is found for the given id
     */
    public CollectionInstrumentMetadataDto getMetadataById(UUID collectionInstrumentId) {
        CollectionInstrumentEntity entity = collectionInstrumentRepository.findById(collectionInstrumentId)
                .orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_MESSAGE + collectionInstrumentId));

        return new CollectionInstrumentMetadataDto(
                entity.getMode(),
                entity.getType(),
                entity.getConceptualModel().getPoguesVersionId()
        );
    }

    /**
     * Retrieves the content of a collection instrument by its identifier.
     *
     * @param collectionInstrumentId the unique identifier of the collection instrument
     * @return the content associated with the instrument
     * @throws IllegalArgumentException if no instrument is found for the given id
     */
    public String getContentById(UUID collectionInstrumentId) {
        return collectionInstrumentRepository.findById(collectionInstrumentId)
                .orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_MESSAGE + collectionInstrumentId))
                .getContent();
    }
}