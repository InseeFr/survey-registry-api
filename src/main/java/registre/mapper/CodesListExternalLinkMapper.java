package registre.mapper;

import org.springframework.stereotype.Component;
import registre.dto.CodesListExternalLink;
import registre.entity.CodesListExternalLinkEntity;

@Component
public class CodesListExternalLinkMapper {

    public CodesListExternalLinkEntity toEntity(CodesListExternalLink dto) {
        if (dto == null) return null;

        CodesListExternalLinkEntity entity = new CodesListExternalLinkEntity();
        entity.setUuid(dto.getUuid());
        entity.setVersion(dto.getVersion());
        return entity;
    }

    public CodesListExternalLink toDto(CodesListExternalLinkEntity entity) {
        if (entity == null) return null;

        CodesListExternalLink dto = new CodesListExternalLink();
        dto.setUuid(entity.getUuid());
        dto.setVersion(entity.getVersion());
        return dto;
    }
}
