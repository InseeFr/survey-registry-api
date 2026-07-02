package fr.insee.surveyregistry.service;

import fr.insee.surveyregistry.dto.CodesListContent;
import fr.insee.surveyregistry.dto.CodesListDto;
import fr.insee.surveyregistry.dto.CodesListMetadataDto;
import fr.insee.surveyregistry.dto.SearchConfig;
import fr.insee.surveyregistry.entity.CodesListEntity;
import fr.insee.surveyregistry.mapper.CodesListMapper;
import fr.insee.surveyregistry.repository.CodesListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CodesListPublicationService {

    private static final String CODES_LIST_NOT_FOUND = "Codes list not found";
    private static final String ALREADY_EXISTS = " already exists";

    private final CodesListRepository codesListRepository;
    private final CodesListMapper codesListMapper;

    /**
     * Creates a codes list with only metadata.
     * The version is automatically incremented based on existing entries with the same theme and referenceYear.
     *
     * @param metadataDto the metadata for the codes list
     */
    @Transactional
    public UUID createCodesListMetadataOnly(CodesListMetadataDto metadataDto) {
        Integer nextVersion = computeNextVersion(metadataDto.theme(), metadataDto.referenceYear());

        CodesListDto dto = new CodesListDto(
                null,
                new CodesListMetadataDto(
                        null,
                        metadataDto.label(),
                        nextVersion,
                        metadataDto.theme(),
                        metadataDto.referenceYear(),
                        metadataDto.urn(),
                        metadataDto.isDeprecated(),
                        metadataDto.isValid(),
                        null
                ),
                null,
                null
        );

        return createCodesList(dto);
    }

    /**
     * Persists a CodesListEntity in the database.
     *
     * @param dto the DTO representing the codes list
     * @return the generated UUID
     */
    public UUID createCodesList(CodesListDto dto) {
        CodesListEntity entity = codesListMapper.toEntity(dto);

        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID());
        }

        if (entity.getVersion() == null) {
            entity.setVersion(computeNextVersion(entity.getTheme(), entity.getReferenceYear()));
        }

        if (codesListRepository.existsByThemeAndReferenceYearAndVersion(
                entity.getTheme(), entity.getReferenceYear(), entity.getVersion())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Codes list with theme=" + entity.getTheme() +
                            ", referenceYear=" + entity.getReferenceYear() +
                            ", version=" + entity.getVersion() + ALREADY_EXISTS
            );
        }

        entity.setValid(true);

        codesListRepository.save(entity);
        return entity.getId();
    }

    /**
     * Computes the next version for a given (theme, referenceYear) pair.
     *
     * @param theme the generic theme
     * @param referenceYear the reference year (nullable)
     * @return next version as Integer
     */
    private Integer computeNextVersion(String theme, String referenceYear) {
        Integer maxVersion = codesListRepository.findMaxVersionByThemeAndReferenceYear(theme, referenceYear);
        return (maxVersion != null) ? maxVersion + 1 : 1;
    }

    /**
     * Deprecates older versions of a codes list (same theme and reference year)
     * except the current one, in a new independent transaction.
     *
     * @param theme the theme of the codes list
     * @param referenceYear the reference year of the codes list
     * @param currentId the ID of the current codes list (not to deprecate)
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deprecateOlderVersions(String theme, String referenceYear, UUID currentId) {
        codesListRepository.deprecateOlderVersions(theme, referenceYear, currentId);
    }

    /**
     * Adds content to an existing codes list.
     *
     * @param codesListId the UUID of the codes list
     * @param content the content object
     */
    public void createContent(UUID codesListId, CodesListContent content) {
        if (!codesListRepository.existsById(codesListId)) {
            throw new IllegalArgumentException(CODES_LIST_NOT_FOUND);
        }

        if (codesListRepository.existsByIdAndContentIsNotNull(codesListId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Content of " + codesListId + ALREADY_EXISTS
            );
        }

        codesListRepository.findById(codesListId).ifPresent(entity -> {
            entity.setContent(content);
            codesListRepository.save(entity);
        });
    }

    /**
     * Adds a search configuration to an existing codes list.
     *
     * @param codesListId the UUID of the codes list
     * @param searchConfig the search configuration object
     */
    public void createSearchConfiguration(UUID codesListId, SearchConfig searchConfig) {
        if (!codesListRepository.existsById(codesListId)) {
            throw new IllegalArgumentException(CODES_LIST_NOT_FOUND);
        }

        if (codesListRepository.existsByIdAndSearchConfigurationIsNotNull(codesListId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Search configuration of " + codesListId + ALREADY_EXISTS
            );
        }

        codesListRepository.findById(codesListId).ifPresent(entity -> {
            entity.setSearchConfiguration(searchConfig);
            codesListRepository.save(entity);
        });
    }

    /**
     * Set the URN of an existing codes list which had none.
     *
     * @param codesListId the UUID of the codes list
     * @param urn the urn string
     */
    public void createURN(UUID codesListId, String urn) {
        if (!codesListRepository.existsById(codesListId)) {
            throw new IllegalArgumentException(CODES_LIST_NOT_FOUND);
        }

        if (codesListRepository.existsByIdAndUrnIsNotNull(codesListId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "URN of " + codesListId + ALREADY_EXISTS
            );
        }

        codesListRepository.findById(codesListId).ifPresent(entity -> {
            entity.setUrn(urn);
            codesListRepository.save(entity);
        });
    }

    /**
     * Marks a codes list as deprecated (sets {@code isDeprecated = true}).
     * This operation can only be performed once: a codes list already marked
     * as deprecated cannot become valid again.
     *
     * @param codesListId the unique identifier of the codes list to deprecate
     * @throws IllegalArgumentException if the codes list does not exist
     * @throws ResponseStatusException  if the codes list is already deprecated
     */
    public void markAsDeprecated(UUID codesListId) {
        if (!codesListRepository.existsById(codesListId)) {
            throw new IllegalArgumentException(CODES_LIST_NOT_FOUND);
        }

        codesListRepository.findById(codesListId).ifPresent(entity -> {
            if (entity.isDeprecated()) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Codes list " + codesListId + " is already deprecated"
                );
            }

            entity.setDeprecated(true);
            codesListRepository.save(entity);
        });
    }

    /**
     * Marks a codes list as invalid (sets {@code isValid = false}).
     * This operation can only be performed once: a codes list already marked
     * as invalid cannot become valid again.
     *
     * @param codesListId the unique identifier of the codes list to invalidate
     * @throws IllegalArgumentException if the codes list does not exist
     * @throws ResponseStatusException  if the codes list is already invalid
     */
    public void markAsInvalid(UUID codesListId) {
        if (!codesListRepository.existsById(codesListId)) {
            throw new IllegalArgumentException("Codes list not found: " + codesListId);
        }

        codesListRepository.findById(codesListId).ifPresent(entity -> {
            if (!entity.isValid()) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Codes list " + codesListId + " is already marked as invalid"
                );
            }

            entity.setValid(false);
            codesListRepository.save(entity);
        });
    }
}