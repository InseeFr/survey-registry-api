package fr.insee.surveyregistry.mapper.conceptualmodel;

import fr.insee.surveyregistry.dto.conceptualmodel.ConceptualModelDto;
import fr.insee.surveyregistry.dto.conceptualmodel.ConceptualModelMetadataDto;
import fr.insee.surveyregistry.entity.ConceptualModelEntity;
import org.springframework.stereotype.Component;

@Component
public class ConceptualModelMapper {

    public ConceptualModelDto toDto(ConceptualModelEntity entity) {
        if (entity == null) return null;

        ConceptualModelMetadataDto metadataDto =
                new ConceptualModelMetadataDto(
                        entity.getPoguesVersionId(),
                        entity.getPoguesId(),
                        entity.getSerieId()
                );

        String ddiContent = entity.getDdiContent();

        return new ConceptualModelDto(
                entity.getPoguesVersionId(),
                metadataDto,
                ddiContent
        );
    }
}
