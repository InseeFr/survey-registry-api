package fr.insee.surveyregistry.mapper;

import fr.insee.surveyregistry.dto.SearchConfig;
import fr.insee.surveyregistry.enums.CodesListMetadataExpandableFieldsEnum;
import jakarta.annotation.Nullable;
import org.springframework.stereotype.Component;
import fr.insee.surveyregistry.dto.CodesListMetadataDto;
import fr.insee.surveyregistry.repository.CodesListRepository.MetadataProjection;

import java.util.List;

@Component
public class CodesListMetadataMapper {

    private final CodesListExternalLinkMapper externalLinkMapper;

    public CodesListMetadataMapper(CodesListExternalLinkMapper externalLinkMapper) {
        this.externalLinkMapper = externalLinkMapper;
    }

    public CodesListMetadataDto toDto(MetadataProjection projection) {
        return toDto(projection, null);
    }

    public CodesListMetadataDto toDto(MetadataProjection projection, @Nullable List<CodesListMetadataExpandableFieldsEnum> expand) {
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
                projection.isValid(),
                computeSearchConfiguration(projection, expand)
        );
    }

    /** Only return the search configuration if the optional field is needed. */
    private SearchConfig computeSearchConfiguration(MetadataProjection projection, @Nullable List<CodesListMetadataExpandableFieldsEnum> expand) {
        if (expand != null && expand.contains(CodesListMetadataExpandableFieldsEnum.SEARCH_CONFIGURATION)) {
            return projection.getSearchConfiguration();
        }
        return null;
    }
}
