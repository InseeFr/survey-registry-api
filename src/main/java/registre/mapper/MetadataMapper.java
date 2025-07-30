package registre.mapper;

import org.springframework.stereotype.Component;
import registre.dto.MetadataDto;
import registre.entity.MetadataEntity;

@Component
public class MetadataMapper {

    private final CodesListExternalLinkMapper externalLinkMapper;

    public MetadataMapper(CodesListExternalLinkMapper externalLinkMapper) {
        this.externalLinkMapper = externalLinkMapper;
    }

    public MetadataEntity toEntity(MetadataDto dto) {
        if (dto == null) return null;

        MetadataEntity entity = new MetadataEntity();
        entity.setId(dto.getId());
        entity.setLabel(dto.getLabel());
        entity.setVersion(dto.getVersion());
        entity.setExternalLink(externalLinkMapper.toEntity(dto.getExternalLink()));
        return entity;
    }

    public MetadataDto toDto(MetadataEntity entity) {
        if (entity == null) return null;

        MetadataDto dto = new MetadataDto();
        dto.setId(entity.getId());
        dto.setLabel(entity.getLabel());
        dto.setVersion(entity.getVersion());
        dto.setExternalLink(externalLinkMapper.toDto(entity.getExternalLink()));
        return dto;
    }
}
