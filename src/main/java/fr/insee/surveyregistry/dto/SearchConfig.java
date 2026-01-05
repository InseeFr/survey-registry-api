package fr.insee.surveyregistry.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Map;

public record SearchConfig(@JsonValue Map<String, Object> content) {}
