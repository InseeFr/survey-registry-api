package registre.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import registre.dto.CodesListDto;
import registre.dto.MetadataDto;
import registre.entity.CodesListEntity;
import registre.mapper.CodesListExternalLinkMapper;
import registre.mapper.CodesListMapper;
import registre.repository.CodesListRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class CodesListRecoveryService {

    private final CodesListRepository codesListRepository;
    private final CodesListMapper codesListMapper;
    private final CodesListExternalLinkMapper codesListExternalLinkMapper;
    public CodesListRecoveryService(
            CodesListRepository codesListRepository,
            CodesListMapper codesListMapper,
            CodesListExternalLinkMapper codesListExternalLinkMapper
    ) {
        this.codesListRepository = codesListRepository;
        this.codesListMapper = codesListMapper;
        this.codesListExternalLinkMapper = codesListExternalLinkMapper;
    }

    public List<MetadataDto> getAllMetadata() {
        return codesListRepository.findAllBy().stream()
                .map(projection -> {
                    MetadataDto dto = new MetadataDto();
                    dto.setId(projection.getId());
                    dto.setLabel(projection.getLabel());
                    dto.setVersion(projection.getVersion());

                    if (projection.getCodesListExternalLink() != null) {
                        dto.setExternalLink(
                                codesListExternalLinkMapper.toDto(projection.getCodesListExternalLink())
                        );
                    }
                    return dto;
                })
                .toList();
    }

    public Optional<JsonNode> getCodesListById(UUID id) {
        return codesListRepository.findById(id)
                .map(CodesListEntity::getContent);
    }

    public Optional<MetadataDto> getMetadataById(UUID id) {
        return codesListRepository.findById(id)
                .map(codesListMapper::toDto)
                .map(CodesListDto::getMetadata);
    }

    public Optional<JsonNode> getSearchConfiguration(UUID id) {
        return codesListRepository.findById(id)
                .map(CodesListEntity::getSearchConfiguration);
    }
}
