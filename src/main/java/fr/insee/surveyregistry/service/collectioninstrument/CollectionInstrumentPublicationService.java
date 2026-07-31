package fr.insee.surveyregistry.service.collectioninstrument;

import fr.insee.surveyregistry.dto.collectioninstrument.CollectionInstrumentDto;
import fr.insee.surveyregistry.dto.collectioninstrument.CollectionInstrumentMetadataDto;
import fr.insee.surveyregistry.entity.CollectionInstrumentEntity;
import fr.insee.surveyregistry.entity.ConceptualModelEntity;
import fr.insee.surveyregistry.enums.CollectionInstrumentMode;
import fr.insee.surveyregistry.exception.InvalidRequestException;
import fr.insee.surveyregistry.exception.ResourceAlreadyExistsException;
import fr.insee.surveyregistry.mapper.collectioninstrument.CollectionInstrumentMapper;
import fr.insee.surveyregistry.repository.CollectionInstrumentRepository;
import fr.insee.surveyregistry.repository.ConceptualModelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CollectionInstrumentPublicationService {

    private final CollectionInstrumentRepository collectionInstrumentRepository;
    private final ConceptualModelRepository conceptualModelRepository;
    private final CollectionInstrumentMapper collectionInstrumentMapper;

    /**
     * Creates a collection instrument with metadata only.
     * The collection instrument ID is automatically generated.
     * The version is automatically incremented for the given Pogues ID and mode.
     * Lunatic and DDI contents are not provided at this stage.
     * The release date remains null until both contents are available.
     *
     * @param metadataDto the collection instrument metadata
     * @return the generated collection instrument ID
     */
    @Transactional
    public UUID createCollectionInstrumentMetadataOnly(CollectionInstrumentMetadataDto metadataDto) {
        CollectionInstrumentDto dto = new CollectionInstrumentDto(
                null,
                new CollectionInstrumentMetadataDto(
                        null,
                        metadataDto.poguesId(),
                        metadataDto.mode(),
                        null,
                        metadataDto.poguesVersionId(),
                        metadataDto.generationParameters(),
                        metadataDto.releaseDescription(),
                        null
                ),
                null,
                null
        );

        return createCollectionInstrument(dto);
    }

    /**
     * Persists a collection instrument.
     * The collection instrument ID is automatically generated.
     * The version is automatically incremented for the given Pogues ID and mode.
     * The release date is set when both Lunatic and DDI contents are available.
     *
     * @param dto the collection instrument to create
     * @return the generated collection instrument ID
     */
    public UUID createCollectionInstrument(CollectionInstrumentDto dto) {
        CollectionInstrumentMetadataDto metadata = dto.metadata();

        ConceptualModelEntity conceptualModel =
                conceptualModelRepository.findById(metadata.poguesId())
                        .orElseThrow(() ->
                                new InvalidRequestException("No conceptual model found for poguesId: "
                                        + metadata.poguesId()
                                )
                        );

        CollectionInstrumentEntity entity = collectionInstrumentMapper.toEntity(dto, conceptualModel);

        if (entity.getCollectionInstrumentId() == null) {
            entity.setCollectionInstrumentId(UUID.randomUUID());
        }

        if (entity.getVersion() == null) {
            entity.setVersion(computeNextVersion(metadata.poguesId(), metadata.mode()));
        }

        if (collectionInstrumentRepository
                .existsByConceptualModel_PoguesIdAndModeAndVersion(
                        metadata.poguesId(),
                        metadata.mode(),
                        entity.getVersion()
                )) {

            throw new ResourceAlreadyExistsException("Collection instrument with poguesId="
                            + metadata.poguesId()
                            + ", mode="
                            + metadata.mode()
                            + ", version="
                            + entity.getVersion()
                            + " already exists"
            );
        }

        if (dto.lunaticContent() != null) {entity.setLunaticContent(dto.lunaticContent().content());}

        entity.setDdiContent(dto.ddiContent());

        // The release date is set only when both contents are available.
        if (entity.getLunaticContent() != null
                && entity.getDdiContent() != null) {
            entity.setReleaseDate(Instant.now());
        }

        collectionInstrumentRepository.save(entity);

        return entity.getCollectionInstrumentId();
    }

    /**
     * Computes the next version for a given Pogues ID and collection mode.
     *
     * @param poguesId the Pogues ID of the conceptual model
     * @param mode the collection mode
     * @return the next version number
     */
    private Integer computeNextVersion(String poguesId, CollectionInstrumentMode mode) {
        return collectionInstrumentRepository
                .findMaxVersionByPoguesIdAndMode(poguesId, mode)
                .map(maxVersion -> maxVersion + 1)
                .orElse(1);
    }
}