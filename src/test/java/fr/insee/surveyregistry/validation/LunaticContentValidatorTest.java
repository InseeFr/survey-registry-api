package fr.insee.surveyregistry.validation;

import fr.insee.surveyregistry.exception.InvalidLunaticContentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LunaticContentValidatorTest {

    private LunaticContentValidator validator;

    @BeforeEach
    void init(){
        validator = new LunaticContentValidator();
    }

    @Nested
    @DisplayName("when lunatic content has no suggesters")
    class NoSuggesters {

        @Test
        @DisplayName("should not throw when lunatic content is null")
        void should_not_throw_when_lunatic_content_is_null() {
            assertThatCode(() -> validator.validate(null))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("should not throw when suggesters key is absent")
        void should_not_throw_when_suggesters_key_is_absent() {
            Map<String, Object> lunaticContent = Map.of("questionnaire", "content");

            assertThatCode(() -> validator.validate(lunaticContent))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("should not throw when suggesters list is empty")
        void should_not_throw_when_suggesters_list_is_empty() {
            Map<String, Object> lunaticContent = Map.of("suggesters", List.of());

            assertThatCode(() -> validator.validate(lunaticContent))
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("when suggesters is not a valid array")
    class InvalidSuggestersStructure {

        @Test
        @DisplayName("should throw when suggesters is not a list")
        void should_throw_when_suggesters_is_not_a_list() {
            Map<String, Object> lunaticContent = Map.of("suggesters", "not-a-list");

            assertThatThrownBy(() -> validator.validate(lunaticContent))
                    .isInstanceOf(InvalidLunaticContentException.class)
                    .hasMessageContaining("suggesters");
        }
    }

    @Nested
    @DisplayName("when a suggester name must be a valid UUID")
    class SuggesterNameValidation {

        @Test
        @DisplayName("should not throw when suggester name is a valid UUID string")
        void should_not_throw_when_suggester_name_is_valid_uuid() {
            Map<String, Object> lunaticContent = Map.of(
                    "suggesters", List.of(
                            Map.of("name", "550e8400-e29b-41d4-a716-446655440000")
                    )
            );

            assertThatCode(() -> validator.validate(lunaticContent))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("should not throw when multiple suggesters all have valid UUID names")
        void should_not_throw_when_multiple_suggesters_have_valid_uuid_names() {
            Map<String, Object> lunaticContent = Map.of(
                    "suggesters", List.of(
                            Map.of("name", "550e8400-e29b-41d4-a716-446655440000"),
                            Map.of("name", "123e4567-e89b-12d3-a456-426614174000")
                    )
            );

            assertThatCode(() -> validator.validate(lunaticContent))
                    .doesNotThrowAnyException();
        }

        @ParameterizedTest(name = "should throw when suggester name \"{0}\" is not a valid UUID")
        @ValueSource(strings = {"not-a-uuid", "12345", "", "550e8400-e29b-41d4-a716"})
        @DisplayName("should throw when suggester name is not a valid UUID")
        void should_throw_when_suggester_name_is_not_a_valid_uuid(String invalidName) {
            Map<String, Object> lunaticContent = Map.of(
                    "suggesters", List.of(
                            Map.of("name", invalidName)
                    )
            );

            assertThatThrownBy(() -> validator.validate(lunaticContent))
                    .isInstanceOf(InvalidLunaticContentException.class);
        }

        @Test
        @DisplayName("should throw when suggester name is missing")
        void should_throw_when_suggester_name_is_missing() {
            Map<String, Object> lunaticContent = Map.of(
                    "suggesters", List.of(
                            Map.of("otherField", "value")
                    )
            );

            assertThatThrownBy(() -> validator.validate(lunaticContent))
                    .isInstanceOf(InvalidLunaticContentException.class);
        }

        @Test
        @DisplayName("should throw when suggester name is not a string")
        void should_throw_when_suggester_name_is_not_a_string() {
            Map<String, Object> lunaticContent = Map.of(
                    "suggesters", List.of(
                            Map.of("name", 12345)
                    )
            );

            assertThatThrownBy(() -> validator.validate(lunaticContent))
                    .isInstanceOf(InvalidLunaticContentException.class);
        }

        @Test
        @DisplayName("should throw when a suggester element is not an object")
        void should_throw_when_suggester_element_is_not_an_object() {
            Map<String, Object> lunaticContent = Map.of(
                    "suggesters", List.of("not-an-object")
            );

            assertThatThrownBy(() -> validator.validate(lunaticContent))
                    .isInstanceOf(InvalidLunaticContentException.class);
        }

        @Test
        @DisplayName("should throw on first invalid suggester among valid ones")
        void should_throw_on_first_invalid_suggester_among_valid_ones() {
            Map<String, Object> lunaticContent = Map.of(
                    "suggesters", List.of(
                            Map.of("name", "550e8400-e29b-41d4-a716-446655440000"),
                            Map.of("name", "invalid-uuid")
                    )
            );

            assertThatThrownBy(() -> validator.validate(lunaticContent))
                    .isInstanceOf(InvalidLunaticContentException.class);
        }
    }
}