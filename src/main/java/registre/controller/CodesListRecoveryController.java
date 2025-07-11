package registre.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import registre.dto.Code;
import registre.dto.Metadata;
import registre.service.CodesListRecoveryService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CodesListRecoveryController implements CodesListRecoveryApi {

    private final CodesListRecoveryService codesListRecoveryService;

    @Override
    public ResponseEntity<List<Metadata>> getAllCodesLists() {
        List<Metadata> metadataList = codesListRecoveryService.getAllMetadata();
        return ResponseEntity.ok(metadataList);
    }

    @Override
    public ResponseEntity<List<Code>> getCodesListById(String codesListId) {
        return codesListRecoveryService.getCodesListById(codesListId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Metadata> getCodesListMetadataById(String codesListId) {
        return codesListRecoveryService.getMetadataById(codesListId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Object> getCodesListSearchConfigById(String codesListId) {
        Object config = codesListRecoveryService.getSearchConfiguration(codesListId);
        return ResponseEntity.ok(config);
    }
}


