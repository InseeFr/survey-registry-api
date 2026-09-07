package fr.insee.surveyregistry.validation;

import java.util.regex.Pattern;

public final class UuidFormatValidator {

    private static final Pattern RFC9562_PATTERN = Pattern.compile(
            "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-8][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$"
    );

    private UuidFormatValidator() {
        // Utility class
    }

    public static boolean isValid(String value) {
        return value != null && RFC9562_PATTERN.matcher(value).matches();
    }
}