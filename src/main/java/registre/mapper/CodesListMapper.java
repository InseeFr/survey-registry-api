package registre.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import registre.dto.CodesListDto;
import registre.entity.CodesListEntity;
import registre.entity.CodesListSearchConfigurationEntity;
import registre.exception.InvalidSearchConfigurationException;

import java.util.stream.Collectors;

@Component
public class CodesListMapper {

    private final MetadataMapper metadataMapper;
    private final CodeMapper codeMapper;
    private final ObjectMapper objectMapper;

    public CodesListMapper(
            MetadataMapper metadataMapper,
            CodeMapper codeMapper,
            ObjectMapper objectMapper) {
        this.metadataMapper = metadataMapper;
        this.codeMapper = codeMapper;
        this.objectMapper = objectMapper;
    }

    public CodesListDto toDto(CodesListEntity entity) {
        if (entity == null) return null;

        CodesListDto dto = new CodesListDto();
        dto.setId(entity.getId());
        dto.setMetadata(metadataMapper.toDto(entity.getMetadata()));

        if (entity.getSearchConfiguration() != null) {
            try {
                Object configObject = objectMapper.readValue(
                        entity.getSearchConfiguration().getJsonContent(),
                        Object.class
                );
                dto.setSearchConfiguration(configObject);
            } catch (JsonProcessingException e) {
                throw new InvalidSearchConfigurationException("Erreur lors du parsing du searchConfiguration JSON", e);
            }
        }

        if (entity.getContent() != null) {
            dto.setContent(entity.getContent().stream()
                    .map(codeMapper::toDto)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public CodesListEntity toEntity(CodesListDto dto) {
        if (dto == null) return null;

        CodesListEntity entity = new CodesListEntity();

        if (dto.getId() != null) {
            entity.setId(dto.getId());
        }

        entity.setMetadata(metadataMapper.toEntity(dto.getMetadata()));

        if (dto.getSearchConfiguration() != null) {
            CodesListSearchConfigurationEntity configEntity = new CodesListSearchConfigurationEntity();
            try {
                configEntity.setJsonContent(objectMapper.writeValueAsString(dto.getSearchConfiguration()));
            } catch (JsonProcessingException e) {
                throw new InvalidSearchConfigurationException("Erreur lors de la sérialisation du searchConfiguration en JSON", e);
            }
            entity.setSearchConfiguration(configEntity);
        }

        if (dto.getContent() != null) {
            entity.setContent(dto.getContent().stream()
                    .map(codeMapper::toEntity)
                    .collect(Collectors.toList()));
        }

        return entity;
    }
}
