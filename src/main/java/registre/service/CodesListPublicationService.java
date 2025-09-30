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
     * POST /codes-lists/metadata-only : Create a codes list using only metadata.
     * Persists the codes list and adds an external link if provided.
     *
     * @param metadataDto the metadata for the codes list
     */
    @Transactional
    public void createCodesListMetadataOnly(MetadataDto metadataDto) {
        CodesListDto dto = new CodesListDto(
                null,
                new MetadataDto(
                        null,
                        metadataDto.label(),
                        metadataDto.version(),
                        metadataDto.theme(),
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

    public UUID createCodesList(CodesListDto dto) {
        CodesListEntity entity = codesListMapper.toEntity(dto);

        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID());
        }

        codesListRepository.save(entity);
        return entity.getId();
    }

    public void createContent(UUID codesListId, CodesListContent content) {
        if (!codesListRepository.existsById(codesListId)) {
            throw new IllegalArgumentException(CODES_LIST_NOT_FOUND);
        }

        if (codesListRepository.existsContent(codesListId)) {
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

    public void createExternalLink(UUID codesListId, CodesListExternalLinkDto externalLinkDto) {
        if (!codesListRepository.existsById(codesListId)) {
            throw new IllegalArgumentException(CODES_LIST_NOT_FOUND);
        }

        if (codesListRepository.existsExternalLink(codesListId)) {
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

    public void createSearchConfiguration(UUID codesListId, SearchConfig searchConfig) {
        if (!codesListRepository.existsById(codesListId)) {
            throw new IllegalArgumentException(CODES_LIST_NOT_FOUND);
        }

        if (codesListRepository.existsSearchConfiguration(codesListId)) {
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
