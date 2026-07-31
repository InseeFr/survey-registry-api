package fr.insee.surveyregistry.dto.codeslist;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;
import java.util.Map;

public record CodesListContentDto(@JsonValue List<Map<String, Object>> items) {}
