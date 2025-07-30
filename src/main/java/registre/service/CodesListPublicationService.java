package registre.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import registre.dto.CodeDto;
import registre.dto.CodesListDto;
import registre.dto.CodesListExternalLinkDto;
import registre.entity.*;
import registre.exception.InvalidSearchConfigurationException;
import registre.mapper.CodeMapper;
import registre.mapper.CodesListExternalLinkMapper;
import registre.mapper.CodesListMapper;
import registre.repository.CodesListRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Service
public class CodesListPublicationService {

    private static final String CODES_LIST_NOT_FOUND = "Codes list not found";

    private final CodesListRepository codesListRepository;
    private final CodesListMapper codesListMapper;
    private final CodeMapper codeMapper;
    private final ObjectMapper objectMapper;
    private final CodesListExternalLinkMapper codesListExternalLinkMapper;

    public CodesListPublicationService(CodesListRepository codesListRepository,
                                       CodesListMapper codesListMapper,
                                       CodeMapper codeMapper,
                                       ObjectMapper objectMapper,
                                       CodesListExternalLinkMapper codesListExternalLinkMapper) {
        this.codesListRepository = codesListRepository;
        this.codesListMapper = codesListMapper;
        this.codeMapper = codeMapper;
        this.objectMapper = objectMapper;
        this.codesListExternalLinkMapper = codesListExternalLinkMapper;
    }

    @Transactional
    public String createCodesList(CodesListDto dto) {
        CodesListEntity entity = codesListMapper.toEntity(dto);
        entity.setId(UUID.randomUUID().toString());
        codesListRepository.save(entity);
        return entity.getId();
    }

    @Transactional
    public void updateContent(String codesListId, List<CodeDto> contentDto) {
        CodesListEntity entity = codesListRepository.findById(codesListId)
                .orElseThrow(() -> new IllegalArgumentException(CODES_LIST_NOT_FOUND));

        List<CodeEntity> contentEntities = contentDto.stream()
                .map(codeMapper::toEntity)
                .collect(Collectors.toCollection(ArrayList::new));

        contentEntities.forEach(code -> code.setCodesList(entity));

        entity.setContent(contentEntities);
        codesListRepository.save(entity);
    }

    @Transactional
    public void updateExternalLink(String codesListId, CodesListExternalLinkDto externalLinkDto) {
        CodesListEntity entity = codesListRepository.findById(codesListId)
                .orElseThrow(() -> new IllegalArgumentException(CODES_LIST_NOT_FOUND));

        MetadataEntity metadata = entity.getMetadata();
        if (metadata != null) {
            CodesListExternalLinkEntity externalLinkEntity = codesListExternalLinkMapper.toEntity(externalLinkDto);
            if (externalLinkEntity.getUuid() == null) {
                externalLinkEntity.setUuid(UUID.randomUUID());
            }
            metadata.setExternalLink(externalLinkEntity);
        }

        codesListRepository.save(entity);
    }

    @Transactional
    public void updateSearchConfiguration(String codesListId, Object configJson) {
        CodesListEntity entity = codesListRepository.findById(codesListId)
                .orElseThrow(() -> new IllegalArgumentException(CODES_LIST_NOT_FOUND));

        CodesListSearchConfigurationEntity configEntity = new CodesListSearchConfigurationEntity();

        try {
            JsonNode node = objectMapper.valueToTree(configJson);

            if (!node.isObject()) {
                throw new InvalidSearchConfigurationException("Le JSON fourni doit être un objet");
            }

            ObjectNode objectNode = (ObjectNode) node;

            String configId;
            if (objectNode.has("id") && !objectNode.get("id").isNull()) {
                configId = objectNode.get("id").asText();
            } else {
                configId = UUID.randomUUID().toString();
                objectNode.put("id", configId);
            }

            String finalJson = objectMapper.writeValueAsString(objectNode);

            configEntity.setId(configId);
            configEntity.setJsonContent(finalJson);

        } catch (IllegalArgumentException | JsonProcessingException e) {
            throw new InvalidSearchConfigurationException("Erreur lors de la manipulation du JSON", e);
        }

        entity.setSearchConfiguration(configEntity);
        codesListRepository.save(entity);
    }

}
