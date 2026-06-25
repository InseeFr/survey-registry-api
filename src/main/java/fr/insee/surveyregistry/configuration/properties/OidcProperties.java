package fr.insee.surveyregistry.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "feature.oidc")
public record OidcProperties(
        boolean enabled,
        String authServerUrl,
        String realm,
        String principalAttribute,
        String roleClaim,
        String clientId,
        String stampClaim,
        String usernameClaim,
        List<String> scopes,
        /* List of valid issuers for user's JWT */
        List<String> issuers) {
}
