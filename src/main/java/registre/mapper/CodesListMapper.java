package registre.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.stereotype.Component;
import registre.dto.Code;
import registre.dto.CodesListDto;
import registre.entity.CodeEntity;
import registre.entity.CodesListEntity;
import registre.exception.InvalidSearchConfigurationException;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Getter
public class CodesListMapper {

    private final CodeMapper codeMapper;
    private final ObjectMapper objectMapper;

    public CodesListMapper(CodeMapper codeMapper, ObjectMapper objectMapper) {
        this.codeMapper = codeMapper;
        this.objectMapper = objectMapper;
    }

    public CodesListEntity toEntity(CodesListDto dto) {
        CodesListEntity entity = new CodesListEntity();

        try {
            if (dto.getSearchConfiguration() != null) {
                entity.setSearchConfiguration(objectMapper.writeValueAsString(dto.getSearchConfiguration()));
            }
        } catch (JsonProcessingException e) {
            throw new InvalidSearchConfigurationException("Erreur lors de la conversion de searchConfiguration en JSON", e);
        }

        if (dto.getContent() != null) {
            List<CodeEntity> codeEntities = dto.getContent().stream()
                    .map(codeMapper::toEntity)
                    .peek(entity::addCode)
                    .collect(Collectors.toList());
            entity.setContent(codeEntities);
        }

        return entity;
    }

    public CodesListDto toDto(CodesListEntity entity) {
        CodesListDto dto = new CodesListDto();

        try {
            if (entity.getSearchConfiguration() != null) {
                Object obj = objectMapper.readValue(entity.getSearchConfiguration(), Object.class);
                dto.setSearchConfiguration(obj);
            }
        } catch (JsonProcessingException e) {
            throw new InvalidSearchConfigurationException("Erreur lors de la lecture de searchConfiguration", e);
        }

        List<Code> codes = entity.getContent().stream()
                .map(codeMapper::toDto)
                .collect(Collectors.toList());
        dto.setContent(codes);

        return dto;
    }
}
