package fr.insee.surveyregistry.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;
import java.util.Map;

public record CodesListContent(@JsonValue List<Map<String, Object>> items) {}
