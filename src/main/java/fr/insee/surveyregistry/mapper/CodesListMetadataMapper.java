package fr.insee.surveyregistry.mapper;

import org.springframework.stereotype.Component;
import fr.insee.surveyregistry.dto.CodesListMetadataDto;
import fr.insee.surveyregistry.repository.CodesListRepository.MetadataProjection;

@Component
public class CodesListMetadataMapper {

    private final CodesListExternalLinkMapper externalLinkMapper;

    public CodesListMetadataMapper(CodesListExternalLinkMapper externalLinkMapper) {
        this.externalLinkMapper = externalLinkMapper;
    }

    public CodesListMetadataDto toDto(MetadataProjection projection) {
        if (projection == null) {
            return null;
        }

        return new CodesListMetadataDto(
                projection.getId(),
                projection.getLabel(),
                projection.getVersion(),
                projection.getTheme(),
                projection.getReferenceYear(),
                projection.getCodesListExternalLink() != null
                        ? externalLinkMapper.toDto(projection.getCodesListExternalLink())
                        : null,
                projection.isDeprecated(),
                projection.isValid()
        );
    }
}
