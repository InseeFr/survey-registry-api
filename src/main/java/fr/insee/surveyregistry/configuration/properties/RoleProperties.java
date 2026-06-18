package fr.insee.surveyregistry.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application.roles")
public record RoleProperties(
        String designer,
        String designerAlternative,
        String admin,
        String webclient
) {
}
