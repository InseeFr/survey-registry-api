package fr.insee.surveyregistry.dto.collectioninstrument;

import java.net.URI;
import java.util.UUID;

public record CollectionInstrumentCodesListDto(
        // id of codesList
        UUID id,
        // url to retrieve the codeList in registry
        URI url
) {
}