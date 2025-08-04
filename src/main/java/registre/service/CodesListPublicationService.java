package registre.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import registre.dto.CodesListDto;
import registre.dto.CodesListExternalLinkDto;
import registre.entity.CodesListEntity;
import registre.entity.CodesListExternalLinkEntity;
import registre.mapper.CodesListMapper;
import registre.repository.CodesListExternalLinkRepository;
import registre.repository.CodesListRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CodesListPublicationService {

    private static final String CODES_LIST_NOT_FOUND = "Codes list not found";

    private final CodesListExternalLinkRepository codesListExternalLinkRepository;
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
            CodesListExternalLinkEntity externalLinkEntity = codesListExternalLinkRepository
                    .findById(externalLinkDto.getId())
                    .orElseThrow(() -> new IllegalArgumentException("External link not found"));

            entity.setCodesListExternalLink(externalLinkEntity);
        } else {
            entity.setCodesListExternalLink(null);
        }

        codesListRepository.save(entity);
    }

    @Transactional
    public void updateSearchConfiguration(String codesListId, JsonNode configJson) {
        CodesListEntity entity = codesListRepository.findById(codesListId)
                .orElseThrow(() -> new IllegalArgumentException(CODES_LIST_NOT_FOUND));

        entity.setSearchConfiguration(configJson);
        codesListRepository.save(entity);
    }
}
