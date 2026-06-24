package fr.insee.surveyregistry.mapper;

import org.springframework.stereotype.Component;
import fr.insee.surveyregistry.dto.CodesListDto;
import fr.insee.surveyregistry.dto.CodesListMetadataDto;
import fr.insee.surveyregistry.entity.CodesListEntity;

@Component
public class CodesListMapper {

    private final CodesListExternalLinkMapper externalLinkMapper;

    public CodesListMapper(CodesListExternalLinkMapper externalLinkMapper) {
        this.externalLinkMapper = externalLinkMapper;
    }

    public CodesListDto toDto(CodesListEntity entity) {
        if (entity == null) return null;

        CodesListMetadataDto metadataDto = new CodesListMetadataDto(
                entity.getId(),
                entity.getLabel(),
                entity.getVersion(),
                entity.getTheme(),
                entity.getReferenceYear(),
                entity.getCodesListExternalLink() != null
                        ? externalLinkMapper.toDto(entity.getCodesListExternalLink())
                        : null,
                entity.isDeprecated(),
                entity.isValid(),
                null
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

        CodesListMetadataDto metadata = dto.metadata();
        entity.setLabel(metadata.label());
        entity.setVersion(metadata.version());
        entity.setTheme(metadata.theme());
        entity.setReferenceYear(metadata.referenceYear());
        entity.setDeprecated(Boolean.TRUE.equals(metadata.isDeprecated()));
        entity.setValid(metadata.isValid() == null || metadata.isValid());

        return entity;
    }
}

