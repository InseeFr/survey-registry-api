package fr.insee.surveyregistry.configuration.cors;

import fr.insee.surveyregistry.configuration.properties.ApplicationProperties;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@AllArgsConstructor
public class CorsConfiguration {

    @Bean
    protected CorsConfigurationSource corsConfigurationSource(ApplicationProperties applicationProperties) {
        org.springframework.web.cors.CorsConfiguration configuration = new org.springframework.web.cors.CorsConfiguration();
        configuration.setAllowedOriginPatterns(applicationProperties.corsOrigins());
        configuration.setAllowedMethods(List.of("GET", "PUT", "POST", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.addExposedHeader("Content-Disposition");
        configuration.setMaxAge(3600L);
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
