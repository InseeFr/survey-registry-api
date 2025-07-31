package registre.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import registre.dto.CodesListDto;
import registre.dto.CodesListExternalLinkDto;
import registre.entity.CodesListEntity;
import registre.exception.InvalidSearchConfigurationException;
import registre.mapper.CodesListMapper;
import registre.repository.CodesListRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CodesListPublicationService {

    private static final String CODES_LIST_NOT_FOUND = "Codes list not found";

    private final CodesListRepository codesListRepository;
    private final CodesListMapper codesListMapper;

    @Transactional
    public String createCodesList(CodesListDto dto) {
        CodesListEntity entity = codesListMapper.toEntity(dto);
        entity.setId(UUID.randomUUID().toString());
        codesListRepository.save(entity);
        return entity.getId();
    }

    @Transactional
    public void updateContent(String codesListId, JsonNode contentJson) {
        CodesListEntity entity = codesListRepository.findById(codesListId)
                .orElseThrow(() -> new IllegalArgumentException(CODES_LIST_NOT_FOUND));

        entity.setContent(contentJson);
        codesListRepository.save(entity);
    }

    @Transactional
    public void updateExternalLink(String codesListId, CodesListExternalLinkDto externalLinkDto) {
        CodesListEntity entity = codesListRepository.findById(codesListId)
                .orElseThrow(() -> new IllegalArgumentException(CODES_LIST_NOT_FOUND));

        if (externalLinkDto != null) {
            entity.setExternalLinkVersion(externalLinkDto.getVersion());
        }

        codesListRepository.save(entity);
    }

    @Transactional
    public void updateSearchConfiguration(String codesListId, JsonNode configJson) {
        CodesListEntity entity = codesListRepository.findById(codesListId)
                .orElseThrow(() -> new IllegalArgumentException(CODES_LIST_NOT_FOUND));

        if (!configJson.isObject()) {
            throw new InvalidSearchConfigurationException("The provided JSON must be an object");
        }

        ObjectNode objectNode = (ObjectNode) configJson;

        if (!objectNode.has("id") || objectNode.get("id").isNull()) {
            objectNode.put("id", UUID.randomUUID().toString());
        }

        entity.setSearchConfiguration(objectNode);
        codesListRepository.save(entity);
    }
}
