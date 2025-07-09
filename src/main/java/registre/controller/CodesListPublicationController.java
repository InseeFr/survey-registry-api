package registre.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import registre.dto.Code;
import registre.dto.CodesListDto;
import registre.dto.CodesListExternalLink;
import registre.service.CodesListService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CodesListPublicationController implements CodesListPublicationApi {

    private final CodesListService codesListService;

    @Override
    public ResponseEntity<Void> createCodesList(CodesListDto codesListDto) {
        codesListService.createCodesListMetadataOnly(codesListDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<Void> createFullCodesList(CodesListDto codesListDto) {
        codesListService.createCodesList(codesListDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<Void> putCodesListContentById(String codesListId, List<Code> code) {
        UUID id = UUID.fromString(codesListId);
        codesListService.updateCodesListContent(id, code);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<Void> putCodesListExternalLinkById(String codesListId, CodesListExternalLink codesListExternalLink) {
        UUID id = UUID.fromString(codesListId);
        codesListService.updateExternalLink(id, codesListExternalLink);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<Void> putCodesListSearchConfigById(String codesListId, Object body) {
        UUID id = UUID.fromString(codesListId);
        codesListService.updateSearchConfiguration(id, body);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}

