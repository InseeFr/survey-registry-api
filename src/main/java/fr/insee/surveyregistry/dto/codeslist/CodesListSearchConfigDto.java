package fr.insee.surveyregistry.dto.codeslist;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Map;

public record CodesListSearchConfigDto(@JsonValue Map<String, Object> content) {}
