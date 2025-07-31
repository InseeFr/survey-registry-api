package registre.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import registre.dto.CodesListDto;
import registre.dto.MetadataDto;
import registre.entity.CodesListEntity;
import registre.mapper.CodesListMapper;
import registre.repository.CodesListRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class CodesListRecoveryService {

    private final CodesListRepository codesListRepository;
    private final CodesListMapper codesListMapper;
    public CodesListRecoveryService(
            CodesListRepository codesListRepository,
            CodesListMapper codesListMapper
    ) {
        this.codesListRepository = codesListRepository;
        this.codesListMapper = codesListMapper;
    }

    public List<MetadataDto> getAllMetadata() {
        return codesListRepository.findAll().stream()
                .map(codesListMapper::toDto)
                .map(CodesListDto::getMetadata)
                .toList();
    }

    public Optional<JsonNode> getCodesListById(String id) {
        return codesListRepository.findById(id)
                .map(CodesListEntity::getContent);
    }

    public Optional<MetadataDto> getMetadataById(String id) {
        return codesListRepository.findById(id)
                .map(codesListMapper::toDto)
                .map(CodesListDto::getMetadata);
    }

    public Optional<JsonNode> getSearchConfiguration(String id) {
        return codesListRepository.findById(id)
                .map(CodesListEntity::getSearchConfiguration);
    }
}
