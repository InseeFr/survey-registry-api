package fr.insee.surveyregistry.service;

import fr.insee.surveyregistry.enums.CodesListMetadataExpandableFieldsEnum;
import jakarta.annotation.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import fr.insee.surveyregistry.dto.CodesListContent;
import fr.insee.surveyregistry.dto.CodesListMetadataDto;
import fr.insee.surveyregistry.dto.SearchConfig;
import fr.insee.surveyregistry.entity.CodesListEntity;
import fr.insee.surveyregistry.mapper.CodesListMetadataMapper;
import fr.insee.surveyregistry.repository.CodesListRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class CodesListRecoveryService {

    private final CodesListRepository codesListRepository;
    private final CodesListMetadataMapper metadataMapper;

    public CodesListRecoveryService(
            CodesListRepository codesListRepository,
            CodesListMetadataMapper metadataMapper
    ) {
        this.codesListRepository = codesListRepository;
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

    /** Return codes list metadata associated to the codes list ID. */
    public Optional<CodesListMetadataDto> getMetadataById(UUID id) {
        return getMetadataById(id, null);
    }

    /**
     * Return codes list metadata associated to the codes list ID, with additional fields to expand.
     *
     * @param id Codes list id to fetch metadata of.
     * @param expand List of additional fields to add. Can be null.
     * @return Codes list metadata.
     */
    public Optional<CodesListMetadataDto> getMetadataById(UUID id, @Nullable List<CodesListMetadataExpandableFieldsEnum> expand) {
        return codesListRepository.findMetadataById(id)
                .map(v -> metadataMapper.toDto(v, expand));
    }

    /** Return the search configuration associated to the codes list ID. */
    public Optional<SearchConfig> getSearchConfiguration(UUID id) {
        return codesListRepository.findById(id)
                .map(CodesListEntity::getSearchConfiguration);
    }
}
