package fr.insee.surveyregistry.service;

import fr.insee.surveyregistry.entity.CollectionInstrumentEntity;
import fr.insee.surveyregistry.entity.ConceptualModelEntity;
import fr.insee.surveyregistry.enums.CollectionInstrumentMode;
import fr.insee.surveyregistry.enums.CollectionInstrumentType;
import fr.insee.surveyregistry.repository.CollectionInstrumentRepository;
import fr.insee.surveyregistry.repository.ConceptualModelRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@NullMarked
@RequiredArgsConstructor
@Transactional
public class CollectionInstrumentPublicationService {

    private static final String NOT_FOUND_MESSAGE = "CollectionInstrument not found: ";
    private static final String CONCEPTUAL_MODEL_NOT_FOUND_MESSAGE = "ConceptualModel not found: ";
    private static final String ALREADY_EXISTS = " already exists";

    private final CollectionInstrumentRepository collectionInstrumentRepository;
    private final ConceptualModelRepository conceptualModelRepository;

    /**
     * Creates a collection instrument with metadata only.
     *
     * @param mode the collection mode
     * @param type the instrument type
     * @param conceptualModelId the identifier of the conceptual model (poguesVersionId)
     * @return generated instrument id
     */
    public UUID createCollectionInstrumentMetadataOnly(CollectionInstrumentMode mode,
                                                       CollectionInstrumentType type,
                                                       UUID conceptualModelId) {

        ConceptualModelEntity model = conceptualModelRepository.findById(conceptualModelId)
                .orElseThrow(() -> new IllegalArgumentException(CONCEPTUAL_MODEL_NOT_FOUND_MESSAGE + conceptualModelId));

        UUID collectionInstrumentId = UUID.randomUUID();

        CollectionInstrumentEntity entity = new CollectionInstrumentEntity();
        entity.setCollectionInstrumentId(collectionInstrumentId);
        entity.setMode(mode);
        entity.setType(type);
        entity.setConceptualModel(model);
        entity.setContent(null);

        collectionInstrumentRepository.save(entity);

        return collectionInstrumentId;
    }

    /**
     * Creates a collection instrument with metadata and content.
     *
     * @param mode the collection mode
     * @param type the instrument type
     * @param conceptualModelId the identifier of the conceptual model (poguesVersionId)
     * @param content the content
     * @return generated instrument id
     */
    public UUID createCollectionInstrument(CollectionInstrumentMode mode,
                                           CollectionInstrumentType type,
                                           UUID conceptualModelId,
                                           String content) {

        ConceptualModelEntity model = conceptualModelRepository.findById(conceptualModelId)
                .orElseThrow(() -> new IllegalArgumentException(CONCEPTUAL_MODEL_NOT_FOUND_MESSAGE + conceptualModelId));

        UUID collectionInstrumentId = UUID.randomUUID();

        CollectionInstrumentEntity entity = new CollectionInstrumentEntity();
        entity.setCollectionInstrumentId(collectionInstrumentId);
        entity.setMode(mode);
        entity.setType(type);
        entity.setConceptualModel(model);
        entity.setContent(content);

        collectionInstrumentRepository.save(entity);

        return collectionInstrumentId;
    }

    /**
     * Adds content to an existing collection instrument.
     * Content can only be added once.
     *
     * @param collectionInstrumentId the instrument id
     * @param content the content to add
     */
    public void createContent(UUID collectionInstrumentId, String content) {

        if (!collectionInstrumentRepository.existsById(collectionInstrumentId)) {
            throw new IllegalArgumentException(NOT_FOUND_MESSAGE + collectionInstrumentId);
        }

        if (collectionInstrumentRepository.existsByCollectionInstrumentIdAndContentIsNotNull(collectionInstrumentId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Content of " + collectionInstrumentId + ALREADY_EXISTS
            );
        }

        collectionInstrumentRepository.findById(collectionInstrumentId).ifPresent(entity -> {
            entity.setContent(content);
            collectionInstrumentRepository.save(entity);
        });
    }
}