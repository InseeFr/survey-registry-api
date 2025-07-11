package registre.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import registre.dto.Code;
import registre.dto.CodesListDto;
import registre.dto.CodesListExternalLink;
import registre.entity.*;
import registre.exception.InvalidSearchConfigurationException;
import registre.mapper.CodeMapper;
import registre.mapper.CodesListExternalLinkMapper;
import registre.mapper.CodesListMapper;
import registre.repository.CodesListRepository;

import java.util.List;
import java.util.UUID;

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
    public void updateContent(String codesListId, List<Code> contentDto) {
        CodesListEntity entity = codesListRepository.findById(codesListId)
                .orElseThrow(() -> new IllegalArgumentException(CODES_LIST_NOT_FOUND));

        List<CodeEntity> contentEntities = contentDto.stream()
                .map(codeMapper::toEntity)
                .toList();

        contentEntities.forEach(code -> code.setCodesList(entity));

        entity.setContent(contentEntities);
        codesListRepository.save(entity);
    }

    @Transactional
    public void updateExternalLink(String codesListId, CodesListExternalLink externalLinkDto) {
        CodesListEntity entity = codesListRepository.findById(codesListId)
                .orElseThrow(() -> new IllegalArgumentException(CODES_LIST_NOT_FOUND));

        MetadataEntity metadata = entity.getMetadata();
        if (metadata != null) {
            CodesListExternalLinkEntity externalLinkEntity = codesListExternalLinkMapper.toEntity(externalLinkDto);
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
            String json = objectMapper.writeValueAsString(configJson);
            configEntity.setJsonContent(json);
        } catch (JsonProcessingException e) {
            throw new InvalidSearchConfigurationException("Erreur de sérialisation JSON", e);
        }

        entity.setSearchConfiguration(configEntity);
        codesListRepository.save(entity);
    }

}
