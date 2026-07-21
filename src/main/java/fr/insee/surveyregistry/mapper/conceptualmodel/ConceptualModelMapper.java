package fr.insee.surveyregistry.mapper.conceptualmodel;

import fr.insee.surveyregistry.dto.conceptualmodel.ConceptualModelDto;
import fr.insee.surveyregistry.entity.ConceptualModelEntity;
import org.springframework.stereotype.Component;

@Component
public class ConceptualModelMapper {

    public ConceptualModelDto toDto(ConceptualModelEntity entity) {
        if (entity == null) return null;

        return new ConceptualModelDto(
                entity.getPoguesId(),
                entity.getSerieId()
        );
    }

    public ConceptualModelEntity toEntity(ConceptualModelDto dto) {
        if (dto == null) return null;

        ConceptualModelEntity entity = new ConceptualModelEntity();
        entity.setPoguesId(dto.poguesId());
        entity.setSerieId(dto.serieId());

        return entity;
    }
}
