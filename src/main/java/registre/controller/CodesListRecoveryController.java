package registre.controller;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import registre.dto.MetadataDto;
import registre.service.CodesListRecoveryService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CodesListRecoveryController implements CodesListRecoveryApi {

    private final CodesListRecoveryService codesListRecoveryService;

    @Override
    public ResponseEntity<List<MetadataDto>> getAllCodesLists() {
        List<MetadataDto> metadataList = codesListRecoveryService.getAllMetadata();
        return ResponseEntity.ok(metadataList);
    }

    @Override
    public ResponseEntity<JsonNode> getCodesListById(String codesListId) {
        return codesListRecoveryService.getCodesListById(codesListId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<MetadataDto> getCodesListMetadataById(String codesListId) {
        return codesListRecoveryService.getMetadataById(codesListId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<JsonNode> getCodesListSearchConfigById(String codesListId) {
        return codesListRecoveryService.getSearchConfiguration(codesListId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}


