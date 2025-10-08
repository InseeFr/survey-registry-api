package registre.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import registre.dto.*;
import registre.entity.CodesListEntity;
import registre.entity.CodesListExternalLinkEntity;
import registre.mapper.CodesListMapper;
import registre.repository.CodesListExternalLinkRepository;
import registre.repository.CodesListRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CodesListPublicationService {

    private static final String CODES_LIST_NOT_FOUND = "Codes list not found";
    private static final String ALREADY_EXISTS = " already exists";

    private final CodesListExternalLinkRepository codesListExternalLinkRepository;
    private final CodesListRepository codesListRepository;
    private final CodesListMapper codesListMapper;

    /**
     * Creates a codes list with only metadata.
     * The version is automatically incremented based on existing entries with the same theme and referenceYear.
     *
     * @param metadataDto the metadata for the codes list
     */
    @Transactional
    public void createCodesListMetadataOnly(MetadataDto metadataDto) {
        Integer nextVersion = getNextVersion(metadataDto.theme(), metadataDto.referenceYear());

        CodesListDto dto = new CodesListDto(
                null,
                new MetadataDto(
                        null,
                        metadataDto.label(),
                        nextVersion,
                        metadataDto.theme(),
                        metadataDto.referenceYear(),
                        metadataDto.externalLink()
                ),
                null,
                null
        );

        UUID id = createCodesList(dto);

        if (metadataDto.externalLink() != null) {
            createExternalLink(id, metadataDto.externalLink());
        }
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
            entity.setVersion(getNextVersion(entity.getTheme(), entity.getReferenceYear()));
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

        codesListRepository.save(entity);
        return entity.getId();
    }

    /**
     * Returns the next version for a given (theme, referenceYear) pair.
     *
     * @param theme the generic theme
     * @param referenceYear the reference year (nullable)
     * @return next version as Integer
     */
    private Integer getNextVersion(String theme, String referenceYear) {
        Integer maxVersion = codesListRepository.findMaxVersionByThemeAndReferenceYear(theme, referenceYear);
        return (maxVersion != null) ? maxVersion + 1 : 1;
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
     * Adds an external link to an existing codes list.
     *
     * @param codesListId the UUID of the codes list
     * @param externalLinkDto the external link DTO
     */
    public void createExternalLink(UUID codesListId, CodesListExternalLinkDto externalLinkDto) {
        if (!codesListRepository.existsById(codesListId)) {
            throw new IllegalArgumentException(CODES_LIST_NOT_FOUND);
        }

        if (codesListRepository.existsByIdAndCodesListExternalLinkIsNotNull(codesListId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "External link of " + codesListId + ALREADY_EXISTS
            );
        }

        codesListRepository.findById(codesListId).ifPresent(entity -> {
            if (externalLinkDto != null) {
                CodesListExternalLinkEntity externalLinkEntity = codesListExternalLinkRepository
                        .findById(externalLinkDto.id())
                        .orElseThrow(() -> new IllegalArgumentException("External link not found"));

                entity.setCodesListExternalLink(externalLinkEntity);
            }
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

}