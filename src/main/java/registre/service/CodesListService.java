package registre.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import registre.dto.Code;
import registre.dto.CodesListDto;
import registre.dto.CodesListExternalLink;
import registre.entity.CodeEntity;
import registre.entity.CodesListEntity;
import registre.mapper.CodesListMapper;
import registre.repository.CodesListRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CodesListService {

    private final CodesListRepository repository;
    private final CodesListMapper mapper;

    public CodesListService(CodesListRepository repository, CodesListMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * Crée une liste de codes avec uniquement les métadonnées
     */
    @Transactional
    public CodesListDto createCodesListMetadataOnly(CodesListDto dto) {
        dto.setContent(null);
        dto.setSearchConfiguration(null);
        return createCodesList(dto);
    }

    /**
     * Crée une liste de codes complète : métadonnées + contenu + configuration
     */
    @Transactional
    public CodesListDto createCodesList(CodesListDto dto) {
        CodesListEntity entity = mapper.toEntity(dto);
        CodesListEntity saved = repository.save(entity);
        return mapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public CodesListDto getCodesListById(UUID id) {
        CodesListEntity entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("CodesList non trouvée : " + id));
        return mapper.toDto(entity);
    }

    @Transactional
    public void updateCodesListContent(UUID id, List<Code> content) {
        CodesListEntity entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("CodesList non trouvée : " + id));

        List<CodeEntity> codeEntities = content.stream()
                .map(mapper.getCodeMapper()::toEntity)
                .collect(Collectors.toList());

        entity.setContent(codeEntities);
        repository.save(entity);
    }

    @Transactional
    public void setExternalLink(UUID codesListId, CodesListExternalLink externalLink) {
        CodesListEntity entity = repository.findById(codesListId)
                .orElseThrow(() -> new IllegalArgumentException("CodesList non trouvée : " + codesListId));

        entity.setExternalUuid(externalLink.getUuid());
        entity.setExternalVersion(externalLink.getVersion());

        repository.save(entity);
    }

    @Transactional
    public void updateSearchConfiguration(UUID id, Object searchConfiguration) {
        CodesListEntity entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("CodesList non trouvée : " + id));
        try {
            String json = mapper.getObjectMapper().writeValueAsString(searchConfiguration);
            entity.setSearchConfiguration(json);
            repository.save(entity);
        } catch (Exception e) {
            throw new IllegalArgumentException("SearchConfiguration invalide", e);
        }
    }

}
