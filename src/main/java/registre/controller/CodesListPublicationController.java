package registre.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import registre.dto.*;
import registre.service.CodesListPublicationService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CodesListPublicationController implements CodesListPublicationApi {

    private final CodesListPublicationService codesListPublicationService;

    @Override
    public ResponseEntity<Void> createCodesListMetadataOnly(@Valid MetadataDto metadataDto) {
        codesListPublicationService.createCodesListMetadataOnly(metadataDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<Void> createFullCodesList(@Valid CodesListDto codesListDto) {
        UUID id = codesListPublicationService.createCodesList(codesListDto);

        if (codesListDto.content() != null && !codesListDto.content().items().isEmpty()) {
            codesListPublicationService.createContent(id, codesListDto.content());
        }

        if (codesListDto.metadata() != null && codesListDto.metadata().externalLink() != null) {
            codesListPublicationService.createExternalLink(id, codesListDto.metadata().externalLink());
        }

        if (codesListDto.searchConfiguration() != null) {
            codesListPublicationService.createSearchConfiguration(id, codesListDto.searchConfiguration());
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<Void> putCodesListContentById(UUID codesListId, @RequestBody CodesListContent content) {
        codesListPublicationService.createContent(codesListId, content);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<Void> putCodesListExternalLinkById(UUID codesListId, @Valid CodesListExternalLinkDto codesListExternalLink) {
        codesListPublicationService.createExternalLink(codesListId, codesListExternalLink);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<Void> putCodesListSearchConfigById(UUID codesListId, @RequestBody SearchConfig searchConfig) {
        codesListPublicationService.createSearchConfiguration(codesListId, searchConfig);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
