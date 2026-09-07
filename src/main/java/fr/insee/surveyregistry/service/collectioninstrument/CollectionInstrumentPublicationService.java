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
import fr.insee.surveyregistry.mapper.collectioninstrument.CollectionInstrumentMapper;
import fr.insee.surveyregistry.repository.CollectionInstrumentRepository;
import fr.insee.surveyregistry.repository.ConceptualModelRepository;
import fr.insee.surveyregistry.validation.LunaticContentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class CollectionInstrumentPublicationService {

    private final CollectionInstrumentRepository collectionInstrumentRepository;
    private final ConceptualModelRepository conceptualModelRepository;
    private final CollectionInstrumentMapper collectionInstrumentMapper;
    private final LunaticContentValidator lunaticContentValidator;

    /**
     * Creates a collection instrument with metadata only.
     * Lunatic content is not provided and release date remains null.
     *
     * @param metadataDto collection instrument metadata
     * @return generated collection instrument identifier
     */
    public UUID createCollectionInstrumentMetadataOnly(
            CollectionInstrumentMetadataDto metadataDto
    ) {
        CollectionInstrumentDto dto = new CollectionInstrumentDto(
                null,
                metadataDto,
                null
        );

        return createCollectionInstrument(dto);
    }

    /**
     * Creates a collection instrument.
     * The collection instrument identifier is generated if absent.
     * The version is automatically incremented for the given Pogues ID and mode.
     * The release date is generated when Lunatic content is provided.
     *
     * @param dto collection instrument to create
     * @return generated collection instrument identifier
     */
    public UUID createCollectionInstrument(CollectionInstrumentDto dto) {
        CollectionInstrumentMetadataDto metadataDto = dto.metadata();

        ConceptualModelEntity conceptualModelEntity =
                conceptualModelRepository.findById(metadataDto.poguesVersionId())
                        .orElseThrow(() ->
                                new InvalidRequestException(
                                        "No conceptual model found for poguesVersionId: "
                                                + metadataDto.poguesVersionId())
                        );


        if (conceptualModelEntity.getDdiContent() == null) {
            throw new InvalidRequestException(
                    "Cannot create collection instrument because conceptual model DDI content is missing for " +
                            "poguesVersionId: " + metadataDto.poguesVersionId()
            );
        }


        CollectionInstrumentEntity collectionInstrumentEntity =
                collectionInstrumentMapper.toEntity(dto, conceptualModelEntity);

        collectionInstrumentEntity.setCollectionInstrumentId(UUID.randomUUID());

        if (collectionInstrumentEntity.getVersion() == null) {
            collectionInstrumentEntity.setVersion(
                    computeNextVersion(
                            conceptualModelEntity.getPoguesId(),
                            metadataDto.mode()
                    )
            );
        }

        if (collectionInstrumentRepository.existsByConceptualModel_PoguesIdAndModeAndVersion(
                        conceptualModelEntity.getPoguesId(),
                        metadataDto.mode(),
                        collectionInstrumentEntity.getVersion()
                )) {

            throw new ResourceAlreadyExistsException("Collection instrument already exists for poguesId="
                            + conceptualModelEntity.getPoguesId()
                            + ", mode="
                            + metadataDto.mode()
                            + ", version="
                            + collectionInstrumentEntity.getVersion()
            );
        }

        if(collectionInstrumentRepository.existsByConceptualModel_PoguesVersionIdAndMode(conceptualModelEntity.getPoguesVersionId(), metadataDto.mode())){
            throw new ResourceAlreadyExistsException("Collection instrument already exists for poguesVersionId="
                    + conceptualModelEntity.getPoguesVersionId()
                    + ", mode="
                    + metadataDto.mode()
            );
        }

        if (dto.lunaticContent() != null) {
            lunaticContentValidator.validate(dto.lunaticContent().lunaticContent());

            collectionInstrumentEntity.setLunaticContent(
                    dto.lunaticContent().lunaticContent()
            );

            collectionInstrumentEntity.setReleaseDate(Instant.now());
        }

        collectionInstrumentRepository.save(collectionInstrumentEntity);

        return collectionInstrumentEntity.getCollectionInstrumentId();
    }

    /**
     * Computes the next collection instrument version.
     * Versions are incremented per Pogues ID and collection mode.
     *
     * @param poguesId conceptual model Pogues identifier
     * @param mode collection instrument mode
     * @return next version number
     */
    private Integer computeNextVersion(String poguesId, CollectionInstrumentMode mode) {
        return collectionInstrumentRepository
                .findMaxVersionByPoguesIdAndMode(poguesId, mode)
                .map(maxVersion -> maxVersion + 1)
                .orElse(1);
    }

    /**
     * Adds Lunatic content to an existing collection instrument.
     * The release date is automatically set when the Lunatic content is added.
     *
     * @param collectionInstrumentId the UUID of the collection instrument
     * @param lunaticContent the Lunatic content object
     * @throws ResourceNotFoundException if the collection instrument does not exist
     * @throws ResourceAlreadyExistsException if Lunatic content already exists
     */
    public void addLunaticContent(UUID collectionInstrumentId, CollectionInstrumentLunaticContentDto lunaticContent) {
        CollectionInstrumentEntity collectionInstrumentEntity =
                collectionInstrumentRepository.findById(collectionInstrumentId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Collection instrument not found for id: " + collectionInstrumentId
                                )
                        );

        if (collectionInstrumentEntity.getLunaticContent() != null) {
            throw new ResourceAlreadyExistsException(
                    "Lunatic content already exists for collectionInstrumentId: " + collectionInstrumentId
            );
        }

        lunaticContentValidator.validate(lunaticContent.lunaticContent());

        collectionInstrumentEntity.setLunaticContent(lunaticContent.lunaticContent());
        collectionInstrumentEntity.setReleaseDate(Instant.now());

        collectionInstrumentRepository.save(collectionInstrumentEntity);
    }
}