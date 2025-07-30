package registre.mapper;

import org.springframework.stereotype.Component;
import registre.dto.CodeDto;

@Component
public class CodeMapper {

    public CodeEntity toEntity(CodeDto dto) {
        if (dto == null) return null;
        CodeEntity entity = new CodeEntity();
        entity.setId(dto.getId());
        entity.setLabel(dto.getLabel());
        return entity;
    }

    public CodeDto toDto(CodeEntity entity) {
        if (entity == null) return null;
        CodeDto dto = new CodeDto();
        dto.setId(entity.getId());
        dto.setLabel(entity.getLabel());
        return dto;
    }
}

