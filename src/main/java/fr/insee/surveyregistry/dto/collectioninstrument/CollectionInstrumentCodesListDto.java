package fr.insee.surveyregistry.dto.collectioninstrument;

import java.net.URI;
import java.util.UUID;

public record CollectionInstrumentCodesListDto(
        // ID of the code list
        UUID id,
        // URL to retrieve the code list from the registry
        URI url
) { }