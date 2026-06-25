package fr.insee.surveyregistry.configuration.swagger;

import fr.insee.surveyregistry.configuration.properties.ApplicationProperties;
import fr.insee.surveyregistry.configuration.properties.OidcProperties;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
@ConditionalOnProperty(value="feature.swagger.enabled", havingValue = "true")
public class SpringDocConfiguration {

    public static final String OAUTH2SCHEME = "oAuth2";
    public static final String BEARERSCHEME = "bearerAuth";

    @Bean
    @ConditionalOnProperty(name = "feature.oidc.enabled", havingValue = "false")
    protected OpenAPI noAuthOpenAPI(ApplicationProperties applicationProperties) {
        return generateOpenAPI(applicationProperties, null);
    }

    @Bean
    @ConditionalOnProperty(name = "feature.oidc.enabled", havingValue = "true")
    protected OpenAPI oidcOpenAPI(OidcProperties oidcProperties, ApplicationProperties applicationProperties) {
        return generateOpenAPI(applicationProperties, oidcProperties)
                .addSecurityItem(new SecurityRequirement().addList(OAUTH2SCHEME, Arrays.asList("read", "write")))
                .addSecurityItem(new SecurityRequirement().addList(BEARERSCHEME, Arrays.asList("read", "write")))
                .components(
                        new Components()
                                .addSecuritySchemes(OAUTH2SCHEME,
                                        new SecurityScheme()
                                                .name(OAUTH2SCHEME)
                                                .type(SecurityScheme.Type.OAUTH2)
                                                .flows(getFlows(oidcProperties))
                                )
                                .addSecuritySchemes(BEARERSCHEME,
                                        new SecurityScheme()
                                                .name(BEARERSCHEME)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                )

                );
    }

    private OpenAPI generateOpenAPI(ApplicationProperties applicationProperties, OidcProperties oidcProperties) {
        String issuersInfo = oidcProperties != null ? "<br/><b>Valid issuers:</b><ul>"+ oidcProperties.issuers().stream().reduce(
                "",
                (acc, issuer) -> acc + "<li><a target=\"_blank\" href=\"" + issuer + "\">"+ issuer + "</li>",
                String::concat
        ) : "";
        return new OpenAPI().info(
                new Info()
                        .title(applicationProperties.name())
                        .description("<h2>Rest Endpoints and services exposing codes-lists and questionnaires for surveys</h2>" + issuersInfo)
                        .version(applicationProperties.version())
        );
    }

    private OAuthFlows getFlows(OidcProperties oidcProperties) {
        String authUrl = oidcProperties.authServerUrl() + "/realms/" + oidcProperties.realm() + "/protocol/openid-connect";

        OAuthFlows flows = new OAuthFlows();
        OAuthFlow flow = new OAuthFlow();
        Scopes scopes = new Scopes();

        for(String scope: oidcProperties.scopes()){
            scopes.addString(scope, scope);
        }

        flow.setAuthorizationUrl(authUrl + "/auth");
        flow.setTokenUrl(authUrl + "/token");
        flow.setRefreshUrl(authUrl + "/token");
        flow.setScopes(scopes);
        return flows.authorizationCode(flow);
    }
}
