package fr.insee.surveyregistry.configuration.auth;

import fr.insee.surveyregistry.configuration.properties.ApplicationProperties;
import fr.insee.surveyregistry.configuration.properties.OidcProperties;
import fr.insee.surveyregistry.configuration.properties.RoleProperties;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.authentication.JwtIssuerAuthenticationManagerResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Spring security configuration when using OIDC auth
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@ConditionalOnProperty(name = "feature.oidc.enabled", havingValue = "true")
@AllArgsConstructor
public class OidcSecurityConfiguration {
    private final PublicSecurityFilterChain publicSecurityFilterChainConfiguration;

    @Bean
    public JwtIssuerAuthenticationManagerResolver authenticationManagerResolver(OidcProperties oidcProperties, RoleProperties roleProperties) {
        // cache for authentication Manager
        Map<String, AuthenticationManager> authenticationManagers = new HashMap<>();

        for (String issuer : oidcProperties.issuers()) {
            NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder
                    .withJwkSetUri(issuer + "/protocol/openid-connect/certs")
                    .build();

            JwtAuthenticationProvider provider = new JwtAuthenticationProvider(jwtDecoder);
            provider.setJwtAuthenticationConverter(jwtAuthenticationConverter(oidcProperties, roleProperties));

            AuthenticationManager manager = new ProviderManager(provider);
            authenticationManagers.put(issuer, manager);
        }

        return new JwtIssuerAuthenticationManagerResolver(authenticationManagers::get);
    }

    /**
     * Configure spring security filter chain to handle OIDC authentication
     *
     * @param http Http Security Object
     * @return the spring security filter
     */
    @Bean
    @Order(2)
    protected SecurityFilterChain filterChain(HttpSecurity http, JwtIssuerAuthenticationManagerResolver authenticationManagerResolver) {
        return http
                .securityMatcher("/**")
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .headers(headers -> headers
                        .xssProtection(xssConfig -> xssConfig.headerValue(XXssProtectionHeaderWriter.HeaderValue.DISABLED))
                        .contentSecurityPolicy(cspConfig -> cspConfig
                                .policyDirectives("default-src 'none'")
                        )
                        .referrerPolicy(referrerPolicy ->
                                referrerPolicy
                                        .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN)
                        ))
                .authorizeHttpRequests(configurer -> configurer
                        .anyRequest()
                        .authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth2 -> oauth2.authenticationManagerResolver(authenticationManagerResolver))
                .build();
    }

    @Bean
    @Order(1)
    protected SecurityFilterChain filterPublicUrlsChain(HttpSecurity http, ApplicationProperties applicationProperties,
                                                        OidcProperties oidcProperties) throws Exception {
        URI authServerUrl = URI.create(oidcProperties.authServerUrl());
        String authServerHost = String.format("%s://%s", authServerUrl.getScheme(), authServerUrl.getHost());
        String authorizedConnectionHost = oidcProperties.enabled() ?
                " " + authServerHost : "";
        return publicSecurityFilterChainConfiguration.buildSecurityPublicFilterChain(http, applicationProperties.publicUrls(), authorizedConnectionHost);
    }

    @Bean
    protected JwtAuthenticationConverter jwtAuthenticationConverter(OidcProperties oidcProperties, RoleProperties roleProperties) {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setPrincipalClaimName(oidcProperties.principalAttribute());
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter(oidcProperties, roleProperties));
        return jwtAuthenticationConverter;
    }

    Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter(OidcProperties oidcProperties, RoleProperties roleProperties) {
        return new GrantedAuthorityConverter(oidcProperties, roleProperties);
    }
}
