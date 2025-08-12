package registre.mapper;

import org.springframework.stereotype.Component;
import registre.dto.CodesListDto;
import registre.dto.MetadataDto;
import registre.entity.CodesListEntity;

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
                entity.getCodesListExternalLink() != null
                        ? externalLinkMapper.toDto(entity.getCodesListExternalLink())
                        : null
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
        entity.setSearchConfiguration(dto.searchConfiguration());
        entity.setContent(dto.content());

        MetadataDto metadata = dto.metadata();
        if (metadata != null) {
            entity.setLabel(metadata.label());
            entity.setVersion(metadata.version());

            if (metadata.externalLink() != null) {
                entity.setCodesListExternalLink(
                        externalLinkMapper.toEntity(metadata.externalLink())
                );
            }
        }

        return entity;
    }
}

