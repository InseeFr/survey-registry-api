package registre.mapper;

import org.springframework.stereotype.Component;
import registre.dto.Code;
import registre.entity.CodeEntity;

@Component
public class CodeMapper {

    public CodeEntity toEntity(Code dto) {
        if (dto == null) return null;
        CodeEntity entity = new CodeEntity();
        entity.setId(dto.getId());
        entity.setLabel(dto.getLabel());
        return entity;
    }

    public Code toDto(CodeEntity entity) {
        if (entity == null) return null;
        Code dto = new Code();
        dto.setId(entity.getId());
        dto.setLabel(entity.getLabel());
        return dto;
    }
}

