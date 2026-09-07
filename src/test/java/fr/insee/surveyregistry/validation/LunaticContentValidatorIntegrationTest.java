package fr.insee.surveyregistry.validation;

import fr.insee.surveyregistry.exception.InvalidLunaticContentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LunaticContentValidatorIntegrationTest {

    private LunaticContentValidator validator;
    private ObjectMapper objectMapper;

    @BeforeEach
    void init() {
        validator = new LunaticContentValidator();
        objectMapper = new ObjectMapper();
    }


    @Test
    @DisplayName("should not throw when JSON suggesters names are valid UUIDs")
    void should_not_throw_when_json_suggesters_names_are_valid_uuids() throws Exception {
        String json = """
                {
                  "suggesters": [
                    { "name": "550e8400-e29b-41d4-a716-446655440000" },
                    { "name": "123e4567-e89b-12d3-a456-426614174000" }
                  ]
                }
                """;

        Map<String, Object> lunaticContent = objectMapper.readValue(json, Map.class);

        assertThatCode(() -> validator.validate(lunaticContent))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("should throw when a JSON suggester name is not a valid UUID")
    void should_throw_when_json_suggester_name_is_not_a_valid_uuid() throws Exception {
        String json = """
                {
                  "suggesters": [
                    { "name": "550e8400-e29b-41d4-a716-446655440000" },
                    { "name": "not-a-uuid" }
                  ]
                }
                """;

        Map<String, Object> lunaticContent = objectMapper.readValue(json, Map.class);

        assertThatThrownBy(() -> validator.validate(lunaticContent))
                .isInstanceOf(InvalidLunaticContentException.class);
    }
}