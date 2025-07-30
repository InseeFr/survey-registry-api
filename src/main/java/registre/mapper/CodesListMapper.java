package registre.mapper;

import org.springframework.stereotype.Component;
import registre.dto.CodesListDto;
import registre.dto.CodesListExternalLinkDto;
import registre.dto.MetadataDto;
import registre.entity.CodesListEntity;

@Component
public class CodesListMapper {

    public CodesListDto toDto(CodesListEntity entity) {
        if (entity == null) return null;

        CodesListDto dto = new CodesListDto();
        dto.setId(entity.getId());
        dto.setSearchConfiguration(entity.getSearchConfiguration());
        dto.setContent(entity.getContent());

        MetadataDto metadataDto = new MetadataDto();
        metadataDto.setLabel(entity.getMetadataLabel());
        metadataDto.setVersion(entity.getMetadataVersion());

        CodesListExternalLinkDto externalLink = new CodesListExternalLinkDto();
        externalLink.setVersion(entity.getExternalLinkVersion());
        metadataDto.setExternalLink(externalLink);

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
            entity.setMetadataLabel(metadata.getLabel());
            entity.setMetadataVersion(metadata.getVersion());
            if (metadata.getExternalLink() != null) {
                entity.setExternalLinkVersion(metadata.getExternalLink().getVersion());
            }
        }

        return entity;
    }
}
