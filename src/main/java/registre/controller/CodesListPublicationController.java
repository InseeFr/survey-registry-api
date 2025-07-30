package registre.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import registre.dto.CodesListDto;
import registre.dto.CodesListExternalLinkDto;
import registre.service.CodesListPublicationService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CodesListPublicationController implements CodesListPublicationApi {

    private final CodesListPublicationService codesListPublicationService;

    @Override
    public ResponseEntity<Void> createCodesList(@Valid CodesListDto codesListDto) {
        codesListPublicationService.createCodesList(codesListDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<Void> createFullCodesList(@Valid CodesListDto codesListDto) {
        String id = codesListPublicationService.createCodesList(codesListDto);
        if (codesListDto.getContent() != null) {
            codesListPublicationService.updateContent(id, codesListDto.getContent());
        }
        if (codesListDto.getMetadata() != null && codesListDto.getMetadata().getExternalLink() != null) {
            codesListPublicationService.updateExternalLink(id, codesListDto.getMetadata().getExternalLink());
        }
        if (codesListDto.getSearchConfiguration() != null) {
            codesListPublicationService.updateSearchConfiguration(id, codesListDto.getSearchConfiguration());
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<Void> putCodesListContentById(String codesListId, @Valid List<CodeDto> code) {
        codesListPublicationService.updateContent(codesListId, code);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<Void> putCodesListExternalLinkById(String codesListId, @Valid CodesListExternalLinkDto codesListExternalLink) {
        codesListPublicationService.updateExternalLink(codesListId, codesListExternalLink);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<Void> putCodesListSearchConfigById(String codesListId, @Valid Object body) {
        codesListPublicationService.updateSearchConfiguration(codesListId, body);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
