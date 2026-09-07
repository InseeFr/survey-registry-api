package fr.insee.surveyregistry.validation;

import fr.insee.surveyregistry.exception.InvalidLunaticContentException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class LunaticContentValidator {

    private static final String SUGGESTERS_KEY = "suggesters";
    private static final String NAME_KEY = "name";

    /**
     * Validates that every suggester's "name" field, if present, is a valid UUID.
     *
     * @param lunaticContent the Lunatic content to validate
     * @throws InvalidLunaticContentException if a suggester name is not a valid UUID
     */
    public void validate(Map<String, Object> lunaticContent) {
        if (lunaticContent == null) {
            return;
        }

        Object suggesters = lunaticContent.get(SUGGESTERS_KEY);
        if (suggesters == null) {
            return;
        }

        if (!(suggesters instanceof List<?> suggestersList)) {
            throw new InvalidLunaticContentException(
                    "Lunatic content field 'suggesters' must be an array"
            );
        }

        suggestersList.forEach(this::validateSuggesterName);
    }

    private void validateSuggesterName(Object suggester) {
        if (!(suggester instanceof Map<?, ?> suggesterMap)) {
            throw new InvalidLunaticContentException(
                    "Each suggester must be a JSON object"
            );
        }

        Object name = suggesterMap.get(NAME_KEY);
        if (name == null) {
            throw new InvalidLunaticContentException(
                    "Suggester is missing required field 'name'"
            );
        }

        if (!(name instanceof String nameValue)) {
            throw new InvalidLunaticContentException(
                    "Suggester 'name' must be a string representing a UUID, got: " + name.getClass().getSimpleName()
            );
        }

        if (!UuidFormatValidator.isValid(nameValue)) {
            throw new InvalidLunaticContentException(
                    "Suggester 'name' must be a valid UUID, got: " + nameValue
            );
        }
    }
}