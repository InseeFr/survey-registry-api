package fr.insee.surveyregistry.mapper.conceptualmodel;

import fr.insee.surveyregistry.dto.conceptualmodel.ConceptualModelMetadataDto;
import fr.insee.surveyregistry.entity.ConceptualModelEntity;
import fr.insee.surveyregistry.repository.ConceptualModelRepository;
import org.springframework.stereotype.Component;

@Component
public class ConceptualModelMetadataMapper {

    public ConceptualModelMetadataDto toDto(ConceptualModelRepository.MetadataProjection projection) {
        if (projection == null) return null;

        return new ConceptualModelMetadataDto(
                projection.getPoguesVersionId(),
                projection.getPoguesId(),
                projection.getSerieId()
        );
    }

    public ConceptualModelEntity toEntity(ConceptualModelMetadataDto dto) {
        if (dto == null) return null;

        ConceptualModelEntity entity = new ConceptualModelEntity();

        entity.setPoguesVersionId(dto.poguesVersionId());
        entity.setPoguesId(dto.poguesId());
        entity.setSerieId(dto.serieId());

        return entity;
    }
}
