package registre.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import registre.dto.Code;
import registre.dto.CodesListDto;
import registre.dto.CodesListExternalLink;
import registre.service.CodesListService;

import javax.validation.Valid;
import java.util.List;

@RestController
public class CodesListPublicationController implements CodesListPublicationApi {

    private final CodesListService codesListService;

    public CodesListPublicationController(CodesListService codesListService) {
        this.codesListService = codesListService;
    }

    @Override
    public ResponseEntity<Void> createCodesList(@Valid CodesListDto codesListDto) {
        codesListService.createCodesList(codesListDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<Void> createFullCodesList(@Valid CodesListDto codesListDto) {
        String id = codesListService.createCodesList(codesListDto);
        if (codesListDto.getContent() != null) {
            codesListService.updateContent(id, codesListDto.getContent());
        }
        if (codesListDto.getMetadata() != null && codesListDto.getMetadata().getExternalLink() != null) {
            codesListService.updateExternalLink(id, codesListDto.getMetadata().getExternalLink());
        }
        if (codesListDto.getSearchConfiguration() != null) {
            codesListService.updateSearchConfiguration(id, codesListDto.getSearchConfiguration());
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<Void> putCodesListContentById(String codesListId, @Valid List<Code> code) {
        codesListService.updateContent(codesListId, code);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<Void> putCodesListExternalLinkById(String codesListId, @Valid CodesListExternalLink codesListExternalLink) {
        codesListService.updateExternalLink(codesListId, codesListExternalLink);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<Void> putCodesListSearchConfigById(String codesListId, @Valid Object body) {
        codesListService.updateSearchConfiguration(codesListId, body);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
