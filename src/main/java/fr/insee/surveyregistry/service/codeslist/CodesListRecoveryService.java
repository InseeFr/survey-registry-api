package fr.insee.surveyregistry.service.codeslist;

import fr.insee.surveyregistry.enums.CodesListMetadataExpandableFieldsEnum;
import jakarta.annotation.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import fr.insee.surveyregistry.dto.codeslist.CodesListContentDto;
import fr.insee.surveyregistry.dto.codeslist.CodesListMetadataDto;
import fr.insee.surveyregistry.dto.codeslist.CodesListSearchConfigDto;
import fr.insee.surveyregistry.entity.CodesListEntity;
import fr.insee.surveyregistry.mapper.codeslist.CodesListMetadataMapper;
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

    /** Returns codes lists metadata, optionally filtered by validity and deprecation status. */
    public List<CodesListMetadataDto> getAllMetadata(
            @Nullable List<CodesListMetadataExpandableFieldsEnum> expand,
            @Nullable Boolean valid,
            @Nullable Boolean deprecated) {

        return codesListRepository.findAllMetadata(valid, deprecated).stream()
                .map(v -> metadataMapper.toDto(v, expand))
                .toList();
    }

    public Optional<CodesListContentDto> getCodesListById(UUID id) {
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
    public Optional<CodesListSearchConfigDto> getSearchConfiguration(UUID id) {
        return codesListRepository.findById(id)
                .map(CodesListEntity::getSearchConfiguration);
    }
}
