package registre.mapper;

import org.springframework.stereotype.Component;
import registre.dto.CodesListExternalLinkDto;
import registre.entity.CodesListExternalLinkEntity;

@Component
public class CodesListExternalLinkMapper {

    public CodesListExternalLinkEntity toEntity(CodesListExternalLinkDto dto) {
        if (dto == null) return null;

        CodesListExternalLinkEntity entity = new CodesListExternalLinkEntity();
        entity.setUuid(dto.getUuid());
        entity.setVersion(dto.getVersion());
        return entity;
    }

    public CodesListExternalLinkDto toDto(CodesListExternalLinkEntity entity) {
        if (entity == null) return null;

        CodesListExternalLinkDto dto = new CodesListExternalLinkDto();
        dto.setUuid(entity.getUuid());
        dto.setVersion(entity.getVersion());
        return dto;
    }
}
