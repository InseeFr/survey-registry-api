package fr.insee.surveyregistry.dto.collectioninstrument;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Map;

public record CollectionInstrumentLunaticContentDto(@JsonValue Map<String, Object> lunaticContent) {}