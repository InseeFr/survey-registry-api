package registre.mapper;

import org.springframework.stereotype.Component;
import registre.dto.CodesListDto;
import registre.dto.MetadataDto;
import registre.entity.CodesListEntity;
import registre.entity.CodesListExternalLinkEntity;

@Component
public class CodesListMapper {

    private final CodesListExternalLinkMapper externalLinkMapper;

    public CodesListMapper(CodesListExternalLinkMapper externalLinkMapper) {
        this.externalLinkMapper = externalLinkMapper;
    }

    public CodesListDto toDto(CodesListEntity entity) {
        if (entity == null) return null;

        CodesListDto dto = new CodesListDto();
        dto.setId(entity.getId());
        dto.setSearchConfiguration(entity.getSearchConfiguration());
        dto.setContent(entity.getContent());

        MetadataDto metadataDto = new MetadataDto();
        metadataDto.setLabel(entity.getLabel());
        metadataDto.setVersion(entity.getVersion());

        if (entity.getCodesListExternalLink() != null) {
            metadataDto.setExternalLink(
                    externalLinkMapper.toDto(entity.getCodesListExternalLink())
            );
        }

        dto.setMetadata(metadataDto);

        return dto;
    }

    public CodesListEntity toEntity(CodesListDto dto) {
        if (dto == null) return null;

        CodesListEntity entity = new CodesListEntity();
        entity.setId(dto.getId());
        entity.setSearchConfiguration(dto.getSearchConfiguration());
        entity.setContent(dto.getContent());

        MetadataDto metadata = dto.getMetadata();
        if (metadata != null) {
            entity.setLabel(metadata.getLabel());
            entity.setVersion(metadata.getVersion());

            if (metadata.getExternalLink() != null) {
                CodesListExternalLinkEntity externalLinkEntity =
                        externalLinkMapper.toEntity(metadata.getExternalLink());
                entity.setCodesListExternalLink(externalLinkEntity);
            }
        }

        return entity;
    }
}
