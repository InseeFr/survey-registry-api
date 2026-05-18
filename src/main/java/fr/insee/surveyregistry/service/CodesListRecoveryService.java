package fr.insee.surveyregistry.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import fr.insee.surveyregistry.dto.CodesListContent;
import fr.insee.surveyregistry.dto.CodesListDto;
import fr.insee.surveyregistry.dto.CodesListMetadataDto;
import fr.insee.surveyregistry.dto.SearchConfig;
import fr.insee.surveyregistry.entity.CodesListEntity;
import fr.insee.surveyregistry.mapper.CodesListMapper;
import fr.insee.surveyregistry.mapper.CodesListMetadataMapper;
import fr.insee.surveyregistry.repository.CodesListRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class CodesListRecoveryService {

    private final CodesListRepository codesListRepository;
    private final CodesListMapper codesListMapper;
    private final CodesListMetadataMapper metadataMapper;
    public CodesListRecoveryService(
            CodesListRepository codesListRepository,
            CodesListMapper codesListMapper,
            CodesListMetadataMapper metadataMapper
    ) {
        this.codesListRepository = codesListRepository;
        this.codesListMapper = codesListMapper;
        this.metadataMapper = metadataMapper;
    }

    public List<CodesListMetadataDto> getAllMetadata() {
        return codesListRepository.findAllBy().stream()
                .map(metadataMapper::toDto)
                .toList();
    }

    public Optional<CodesListContent> getCodesListById(UUID id) {
        return codesListRepository.findById(id)
                .map(CodesListEntity::getContent);
    }

    public Optional<CodesListMetadataDto> getMetadataById(UUID id) {
        return codesListRepository.findById(id)
                .map(codesListMapper::toDto)
                .map(CodesListDto::metadata);
    }

    public Optional<SearchConfig> getSearchConfiguration(UUID id) {
        return codesListRepository.findById(id)
                .map(CodesListEntity::getSearchConfiguration);
    }
}
