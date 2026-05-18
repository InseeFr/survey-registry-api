package fr.insee.surveyregistry.dto;

import fr.insee.surveyregistry.enums.CollectionInstrumentMode;
import fr.insee.surveyregistry.enums.CollectionInstrumentType;

import java.util.UUID;

public record CollectionInstrumentMetadataDto(
        CollectionInstrumentMode mode,
        CollectionInstrumentType type,
        UUID conceptualModelId
) {}