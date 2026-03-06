package fr.insee.surveyregistry.mapper;

import org.springframework.stereotype.Component;
import fr.insee.surveyregistry.dto.CodesListExternalLinkDto;
import fr.insee.surveyregistry.entity.CodesListExternalLinkEntity;

@Component
public class CodesListExternalLinkMapper {

    public CodesListExternalLinkEntity toEntity(CodesListExternalLinkDto dto) {
        if (dto == null) return null;

        CodesListExternalLinkEntity entity = new CodesListExternalLinkEntity();
        entity.setId(dto.id());
        return entity;
    }

    public CodesListExternalLinkDto toDto(CodesListExternalLinkEntity entity) {
        if (entity == null) return null;

        return new CodesListExternalLinkDto(
                entity.getId()
        );
    }
}
