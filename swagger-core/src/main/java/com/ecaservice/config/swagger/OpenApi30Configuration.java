package com.ecaservice.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

/**
 * Open api configuration.
 *
 * @author Roman Batygin
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(OpenApiProperties.class)
@RequiredArgsConstructor
public class OpenApi30Configuration {

    public static final String ECA_AUTHENTICATION_SECURITY_SCHEME = "EcaAuth";

    private static final String TOKEN_URL_FORMAT = "%s/oauth/token";

    private final OpenApiProperties openApiProperties;

    /**
     * Creates open api bean.
     *
     * @return open api bean
     */
    @Bean
    public OpenAPI customOpenAPI() {
        log.info("Starting to init Open API configuration");
        return new OpenAPI()
                .components(components())
                .addServersItem(new Server().url(openApiProperties.getBasePath()))
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title(openApiProperties.getTitle())
                .version(openApiProperties.getProjectVersion())
                .description(openApiProperties.getDescription())
                .contact(new Contact()
                        .name(openApiProperties.getAuthor())
                        .email(openApiProperties.getEmail()));
    }

    private Components components() {
        var components = new Components();
        if (!CollectionUtils.isEmpty(openApiProperties.getApiAuth())) {
            log.info("Starting to init security schemes for Open API");
            components.addSecuritySchemes(ECA_AUTHENTICATION_SECURITY_SCHEME, oauth2SecurityScheme());
        }
        return components;
    }

    private SecurityScheme oauth2SecurityScheme() {
        var securityScheme = new SecurityScheme()
                .name(ECA_AUTHENTICATION_SECURITY_SCHEME)
                .type(SecurityScheme.Type.OAUTH2)
                .flows(new OAuthFlows());
        openApiProperties.getApiAuth().forEach(
                (grantType, openApiAuthProperties) -> grantType.handle(new GrantTypeVisitor() {
                    @Override
                    public void visitPassword() {
                        securityScheme.getFlows().setPassword(passwordFlow(openApiAuthProperties));
                        log.info("Password security scheme has been initialized for Open API");
                    }

                    @Override
                    public void visitClientCredentials() {
                        securityScheme.getFlows().setClientCredentials(clientCredentialsFlow(openApiAuthProperties));
                        log.info("Client credentials security scheme has been initialized for Open API");
                    }
                }));
        return securityScheme;
    }

    private OAuthFlow passwordFlow(OpenApiAuthProperties openApiAuthProperties) {
        String tokenUrl = getTokenUrl();
        return new OAuthFlow()
                .tokenUrl(tokenUrl)
                .refreshUrl(tokenUrl)
                .scopes(scopes(openApiAuthProperties));
    }

    private OAuthFlow clientCredentialsFlow(OpenApiAuthProperties openApiAuthProperties) {
        return new OAuthFlow()
                .tokenUrl(getTokenUrl())
                .scopes(scopes(openApiAuthProperties));
    }

    private String getTokenUrl() {
        return String.format(TOKEN_URL_FORMAT, openApiProperties.getTokenBaseUrl());
    }

    private Scopes scopes(OpenApiAuthProperties openApiAuthProperties) {
        var scopes = new Scopes();
        if (!CollectionUtils.isEmpty(openApiAuthProperties.getScopes())) {
            openApiAuthProperties.getScopes().forEach(scope -> scopes.addString(scope, StringUtils.EMPTY));
        }
        return scopes;
    }
}
