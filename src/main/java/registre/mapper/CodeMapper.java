package registre.mapper;

import org.springframework.stereotype.Component;
import registre.dto.Code;
import registre.entity.CodeEntity;

@Component
public class CodeMapper {

    public CodeEntity toEntity(Code dto) {
        CodeEntity entity = new CodeEntity();
        entity.setCode(dto.getId());
        entity.setLabel(dto.getLabel());
        return entity;
    }

    public Code toDto(CodeEntity entity) {
        Code dto = new Code();
        dto.setId(entity.getCode());
        dto.setLabel(entity.getLabel());
        return dto;
    }
}

