package registre.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import registre.dto.CodesListDto;
import registre.dto.MetadataDto;
import registre.entity.CodesListEntity;
import registre.mapper.CodesListMapper;
import registre.mapper.MetadataMapper;
import registre.repository.CodesListRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class CodesListRecoveryService {

    private final CodesListRepository codesListRepository;
    private final CodesListMapper codesListMapper;
    private final MetadataMapper metadataMapper;
    public CodesListRecoveryService(
            CodesListRepository codesListRepository,
            CodesListMapper codesListMapper,
            MetadataMapper metadataMapper
    ) {
        this.codesListRepository = codesListRepository;
        this.codesListMapper = codesListMapper;
        this.metadataMapper = metadataMapper;
    }

    public List<MetadataDto> getAllMetadata() {
        return codesListRepository.findAllBy().stream()
                .map(metadataMapper::toDto)
                .toList();
    }

    public Optional<JsonNode> getCodesListById(UUID id) {
        return codesListRepository.findById(id)
                .map(CodesListEntity::getContent);
    }

    public Optional<MetadataDto> getMetadataById(UUID id) {
        return codesListRepository.findById(id)
                .map(codesListMapper::toDto)
                .map(CodesListDto::metadata);
    }

    public Optional<JsonNode> getSearchConfiguration(UUID id) {
        return codesListRepository.findById(id)
                .map(CodesListEntity::getSearchConfiguration);
    }
}
