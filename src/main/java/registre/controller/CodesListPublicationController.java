package registre.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import registre.dto.CodesListDto;
import registre.dto.CodesListExternalLinkDto;
import registre.service.CodesListPublicationService;

import jakarta.validation.Valid;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CodesListPublicationController implements CodesListPublicationApi {

    private final CodesListPublicationService codesListPublicationService;
    private final ObjectMapper objectMapper;

    @Override
    public ResponseEntity<Void> createCodesList(@Valid CodesListDto codesListDto) {
        codesListPublicationService.createCodesList(codesListDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<Void> createFullCodesList(@Valid CodesListDto codesListDto) {
        UUID id = codesListPublicationService.createCodesList(codesListDto);

        if (codesListDto.content() != null && !codesListDto.content().isEmpty()) {
            codesListPublicationService.updateContent(id, codesListDto.content());
        }

        if (codesListDto.metadata() != null && codesListDto.metadata().externalLink() != null) {
            codesListPublicationService.updateExternalLink(id, codesListDto.metadata().externalLink());
        }

        if (codesListDto.searchConfiguration() != null && !codesListDto.searchConfiguration().isEmpty()) {
            codesListPublicationService.updateSearchConfiguration(id, codesListDto.searchConfiguration());
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<Void> putCodesListContentById(UUID codesListId, @Valid JsonNode body) {
        codesListPublicationService.updateContent(codesListId, body);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<Void> putCodesListExternalLinkById(UUID codesListId, @Valid CodesListExternalLinkDto codesListExternalLink) {
        codesListPublicationService.updateExternalLink(codesListId, codesListExternalLink);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<Void> putCodesListSearchConfigById(UUID codesListId, Object body) {
        JsonNode jsonNode = objectMapper.valueToTree(body);
        codesListPublicationService.updateSearchConfiguration(codesListId, jsonNode);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
