package fr.insee.surveyregistry.mapper.collectioninstrument;

import fr.insee.surveyregistry.dto.collectioninstrument.CollectionInstrumentMetadataDto;
import fr.insee.surveyregistry.repository.CollectionInstrumentRepository;
import org.springframework.stereotype.Component;

@Component
public class CollectionInstrumentMetadataMapper {

    public CollectionInstrumentMetadataDto toDto(CollectionInstrumentRepository.MetadataProjection projection) {
        if (projection == null) {
            return null;
        }

        return new CollectionInstrumentMetadataDto(
                projection.getCollectionInstrumentId(),
                projection.getPoguesId(),
                projection.getMode(),
                projection.getVersion(),
                projection.getPoguesVersionId(),
                projection.getGenerationParameters(),
                projection.getReleaseDescription(),
                projection.getReleaseDate()
        );
    }
}
