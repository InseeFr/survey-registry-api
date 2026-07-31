package fr.insee.surveyregistry.mapper.collectioninstrument;

import fr.insee.surveyregistry.dto.collectioninstrument.CollectionInstrumentDto;
import fr.insee.surveyregistry.dto.collectioninstrument.CollectionInstrumentLunaticContentDto;
import fr.insee.surveyregistry.dto.collectioninstrument.CollectionInstrumentMetadataDto;
import fr.insee.surveyregistry.entity.CollectionInstrumentEntity;
import fr.insee.surveyregistry.entity.ConceptualModelEntity;
import org.springframework.stereotype.Component;

@Component
public class CollectionInstrumentMapper {

    public CollectionInstrumentDto toDto(CollectionInstrumentEntity entity) {
        if (entity == null) return null;

        CollectionInstrumentMetadataDto metadataDto =
                new CollectionInstrumentMetadataDto(
                        entity.getCollectionInstrumentId(),
                        entity.getConceptualModel().getPoguesId(),
                        entity.getMode(),
                        entity.getVersion(),
                        entity.getPoguesVersionId(),
                        entity.getGenerationParameters(),
                        entity.getReleaseDescription(),
                        entity.getReleaseDate()
                );

        CollectionInstrumentLunaticContentDto lunaticContent =
                entity.getLunaticContent() != null
                        ? new CollectionInstrumentLunaticContentDto(
                        entity.getLunaticContent()
                )
                        : null;

        return new CollectionInstrumentDto(
                entity.getCollectionInstrumentId(),
                metadataDto,
                lunaticContent,
                entity.getDdiContent()
        );
    }

    public CollectionInstrumentEntity toEntity(CollectionInstrumentDto dto, ConceptualModelEntity conceptualModel) {
        if (dto == null) return null;

        CollectionInstrumentEntity entity = new CollectionInstrumentEntity();

        entity.setCollectionInstrumentId(dto.collectionInstrumentId());

        entity.setConceptualModel(conceptualModel);

        CollectionInstrumentMetadataDto metadata = dto.metadata();

        entity.setMode(metadata.mode());
        entity.setVersion(metadata.version());
        entity.setPoguesVersionId(metadata.poguesVersionId());
        entity.setGenerationParameters(metadata.generationParameters());
        entity.setReleaseDescription(metadata.releaseDescription());

        return entity;
    }
}