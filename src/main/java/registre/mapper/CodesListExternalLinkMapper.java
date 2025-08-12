package registre.mapper;

import org.springframework.stereotype.Component;
import registre.dto.CodesListExternalLinkDto;
import registre.entity.CodesListExternalLinkEntity;

@Component
public class CodesListExternalLinkMapper {

    public CodesListExternalLinkEntity toEntity(CodesListExternalLinkDto dto) {
        if (dto == null) return null;

        CodesListExternalLinkEntity entity = new CodesListExternalLinkEntity();
        entity.setId(dto.id());
        entity.setVersion(dto.version());
        return entity;
    }

    public CodesListExternalLinkDto toDto(CodesListExternalLinkEntity entity) {
        if (entity == null) return null;

        return new CodesListExternalLinkDto(
                entity.getId(),
                entity.getVersion()
        );
    }
}
