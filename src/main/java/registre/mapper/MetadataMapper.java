package registre.mapper;

import org.springframework.stereotype.Component;
import registre.dto.MetadataDto;
import registre.repository.CodesListRepository.MetadataProjection;

@Component
public class MetadataMapper {

    private final CodesListExternalLinkMapper externalLinkMapper;

    public MetadataMapper(CodesListExternalLinkMapper externalLinkMapper) {
        this.externalLinkMapper = externalLinkMapper;
    }

    public MetadataDto toDto(MetadataProjection projection) {
        if (projection == null) {
            return null;
        }

        return new MetadataDto(
                projection.getId(),
                projection.getLabel(),
                projection.getVersion(),
                projection.getTheme(),
                projection.getReferenceYear(),
                projection.getCodesListExternalLink() != null
                        ? externalLinkMapper.toDto(projection.getCodesListExternalLink())
                        : null
        );
    }
}
