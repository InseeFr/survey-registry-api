package fr.insee.surveyregistry.mapper;

import org.springframework.stereotype.Component;
import fr.insee.surveyregistry.dto.CodesListDto;
import fr.insee.surveyregistry.dto.MetadataDto;
import fr.insee.surveyregistry.entity.CodesListEntity;

@Component
public class CodesListMapper {

    private final CodesListExternalLinkMapper externalLinkMapper;

    public CodesListMapper(CodesListExternalLinkMapper externalLinkMapper) {
        this.externalLinkMapper = externalLinkMapper;
    }

    public CodesListDto toDto(CodesListEntity entity) {
        if (entity == null) return null;

        MetadataDto metadataDto = new MetadataDto(
                entity.getId(),
                entity.getLabel(),
                entity.getVersion(),
                entity.getTheme(),
                entity.getReferenceYear(),
                entity.getCodesListExternalLink() != null
                        ? externalLinkMapper.toDto(entity.getCodesListExternalLink())
                        : null,
                entity.isDeprecated(),
                entity.isValid()
        );

        return new CodesListDto(
                entity.getId(),
                metadataDto,
                entity.getSearchConfiguration(),
                entity.getContent()
        );
    }

    public CodesListEntity toEntity(CodesListDto dto) {
        if (dto == null) return null;

        CodesListEntity entity = new CodesListEntity();
        entity.setId(dto.id());

        MetadataDto metadata = dto.metadata();
        entity.setLabel(metadata.label());
        entity.setVersion(metadata.version());
        entity.setTheme(metadata.theme());
        entity.setReferenceYear(metadata.referenceYear());
        entity.setDeprecated(metadata.isDeprecated());
        entity.setValid(metadata.isValid());

        return entity;
    }
}

