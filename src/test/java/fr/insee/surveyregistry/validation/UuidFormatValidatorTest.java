package fr.insee.surveyregistry.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UuidFormatValidatorTest {

    @Nested
    @DisplayName("when the value is a well-formed RFC 9562 UUID")
    class ValidUuid {

        @ParameterizedTest
        @ValueSource(strings = {
                "3f2504e0-4f89-41d3-9a0c-0305e82c3301", // version 4
                "6ba7b810-9dad-11d1-80b4-00c04fd430c8", // version 1
                "6ba7b811-9dad-31d1-90b4-00c04fd430c8", // version 3
                "6ba7b812-9dad-51d1-a0b4-00c04fd430c8", // version 5
                "017f22e2-79b0-6cc3-98c4-dc0c0c07398f", // version 6
                "017f22e2-79b0-7cc3-98c4-dc0c0c07398f", // version 7
                "017f22e2-79b0-8cc3-98c4-dc0c0c07398f",  // version 8
                "3F2504E0-4F89-41D3-9A0C-0305E82C3301", // uppercase
                "7eb32ba0-0d82-4e6e-bd77-fa9d399668fb"  // JC test :p
        })
        @DisplayName("should return true")
        void should_return_true_when_uuid_is_valid(String value) {
            boolean result = UuidFormatValidator.isValid(value);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("should return true with generated value")
        void should_return_true_when_uuid_is_generated() {
            boolean result = UuidFormatValidator.isValid(UUID.randomUUID().toString());

            assertThat(result).isTrue();
        }

    }

    @Nested
    @DisplayName("when the value is not a well-formed RFC 9562 UUID")
    class InvalidUuid {

        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {
                "",
                "not-a-uuid",
                "3f2504e0-4f89-41d3-9a0c-0305e82c330",     // too short
                "3f2504e0-4f89-41d3-9a0c-0305e82c33011",   // too long
                "3f2504e0-4f89-01d3-9a0c-0305e82c3301",    // invalid version (0)
                "3f2504e0-4f89-41d3-c9c0-0305e82c3301",    // invalid variant
                "3f2504e04f8941d39a0c0305e82c3301",        // missing dashes
                "3f2504e0_4f89_41d3_9a0c_0305e82c3301",    // wrong separator
                "7eb32ba0-0d82-4e6e-bd77-fa9d9668fb"       // JC test :p
        })
        @DisplayName("should return false")
        void should_return_false_when_uuid_is_invalid(String value) {
            boolean result = UuidFormatValidator.isValid(value);

            assertThat(result).isFalse();
        }
    }

    @Test
    @DisplayName("should not be instantiable")
    void should_not_be_instantiable() {
        assertThat(UuidFormatValidator.class.getDeclaredConstructors())
                .hasSize(1)
                .allSatisfy(constructor -> assertThat(constructor.canAccess(null)).isFalse());
    }
}