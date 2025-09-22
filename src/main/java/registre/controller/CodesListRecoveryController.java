package registre.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import registre.dto.MetadataDto;
import registre.service.CodesListRecoveryService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    public ResponseEntity<List<Map<String,Object>>> getCodesListById(UUID codesListId) {
        return codesListRecoveryService.getCodesListById(codesListId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<MetadataDto> getCodesListMetadataById(UUID codesListId) {
        return codesListRecoveryService.getMetadataById(codesListId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Object> getCodesListSearchConfigById(UUID codesListId) {
        return codesListRecoveryService.getSearchConfiguration(codesListId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}


