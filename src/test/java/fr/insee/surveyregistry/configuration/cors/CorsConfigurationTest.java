package fr.insee.surveyregistry.configuration.cors;

import fr.insee.surveyregistry.configuration.properties.ApplicationProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

class CorsConfigurationTest {

    private final CorsConfiguration corsConfiguration = new CorsConfiguration();

    private static ApplicationProperties applicationPropertiesWithCorsOrigins(List<String> corsOrigins) {
        return new ApplicationProperties(
                "localhost",
                "https",
                "Survey Registry",
                "description",
                new String[]{},
                "survey-registry",
                "1.0.0",
                corsOrigins
        );
    }

    private static MockHttpServletRequest requestFor(String path) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI(path);
        return request;
    }

    private final ApplicationProperties applicationProperties =
            applicationPropertiesWithCorsOrigins(List.of("https://app.insee.fr"));

    @Test
    @DisplayName("should allow configured origin patterns")
    void should_allow_configured_origin_patterns() {
        // When
        CorsConfigurationSource source = corsConfiguration.corsConfigurationSource(applicationProperties);
        org.springframework.web.cors.CorsConfiguration configuration = source.getCorsConfiguration(requestFor("/collection-instruments"));

        // Then
        Assertions.assertNotNull(configuration);
        assertThat(configuration.getAllowedOriginPatterns())
                .containsExactly("https://app.insee.fr");
    }

    @Test
    @DisplayName("should restrict allowed HTTP methods to expected verbs")
    void should_restrict_allowed_methods() {
        // When
        org.springframework.web.cors.CorsConfiguration configuration = corsConfiguration
                .corsConfigurationSource(applicationProperties)
                .getCorsConfiguration(requestFor("/collection-instruments"));

        // Then
        Assertions.assertNotNull(configuration);
        assertThat(configuration.getAllowedMethods())
                .containsExactlyInAnyOrder("GET", "PUT", "POST", "DELETE", "OPTIONS");
    }

    @Test
    @DisplayName("should allow only Authorization and Content-Type headers")
    void should_restrict_allowed_headers() {
        // When
        org.springframework.web.cors.CorsConfiguration configuration = corsConfiguration
                .corsConfigurationSource(applicationProperties)
                .getCorsConfiguration(requestFor("/collection-instruments"));

        // Then
        Assertions.assertNotNull(configuration);
        assertThat(configuration.getAllowedHeaders())
                .containsExactlyInAnyOrder("Authorization", "Content-Type");
    }

    @Test
    @DisplayName("should expose Content-Disposition header for file downloads")
    void should_expose_content_disposition_header() {
        // When
        org.springframework.web.cors.CorsConfiguration configuration = corsConfiguration
                .corsConfigurationSource(applicationProperties)
                .getCorsConfiguration(requestFor("/collection-instruments"));

        // Then
        Assertions.assertNotNull(configuration);
        assertThat(configuration.getExposedHeaders())
                .containsExactly("Content-Disposition");
    }

    @Test
    @DisplayName("should allow credentials to support authenticated cross-origin requests")
    void should_allow_credentials() {
        // When
        org.springframework.web.cors.CorsConfiguration configuration = corsConfiguration
                .corsConfigurationSource(applicationProperties)
                .getCorsConfiguration(requestFor("/collection-instruments"));

        // Then
        Assertions.assertNotNull(configuration);
        assertThat(configuration.getAllowCredentials()).isTrue();
    }

    @Test
    @DisplayName("should apply CORS configuration to any application path")
    void should_apply_configuration_to_any_path() {
        // When
        CorsConfigurationSource source = corsConfiguration.corsConfigurationSource(applicationProperties);

        // Then
        assertThat(Objects.requireNonNull(source.getCorsConfiguration(requestFor("/anything/nested/path")))).isNotNull();
    }

    @Test
    @DisplayName("should allow all configured origin patterns")
    void should_allow_all_configured_origin_patterns() {
        // Given
        ApplicationProperties applicationPropertiesOverride = applicationPropertiesWithCorsOrigins(
                List.of("https://app.insee.fr", "https://preprod.insee.fr")
        );

        // When
        org.springframework.web.cors.CorsConfiguration configuration = corsConfiguration
                .corsConfigurationSource(applicationPropertiesOverride)
                .getCorsConfiguration(requestFor("/collection-instruments"));

        // Then
        Assertions.assertNotNull(configuration);
        assertThat(configuration.getAllowedOriginPatterns())
                .containsExactlyInAnyOrder("https://app.insee.fr", "https://preprod.insee.fr");
    }
}