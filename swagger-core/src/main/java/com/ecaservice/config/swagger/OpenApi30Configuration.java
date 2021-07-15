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
@Configuration
@EnableConfigurationProperties(OpenApiProperties.class)
public class OpenApi30Configuration {

    public static final String ECA_AUTHENTICATION_SECURITY_SCHEME = "EcaAuth";

    /**
     * Creates open api bean.
     *
     * @param openApiProperties - open api properties
     * @return open api bean
     */
    @Bean
    public OpenAPI customOpenAPI(OpenApiProperties openApiProperties) {
        return new OpenAPI()
                .components(components(openApiProperties))
                .addServersItem(new Server().url(openApiProperties.getBasePath()))
                .info(apiInfo(openApiProperties));
    }

    private Info apiInfo(OpenApiProperties openApiProperties) {
        return new Info()
                .title(openApiProperties.getTitle())
                .version(openApiProperties.getProjectVersion())
                .description(openApiProperties.getDescription())
                .contact(new Contact()
                        .name(openApiProperties.getAuthor())
                        .email(openApiProperties.getEmail()));
    }

    private Components components(OpenApiProperties openApiProperties) {
        var components = new Components();
        if (!CollectionUtils.isEmpty(openApiProperties.getApiAuth())) {
            System.out.println(openApiProperties.getApiAuth().size());
            components.addSecuritySchemes(ECA_AUTHENTICATION_SECURITY_SCHEME, oauth2SecurityScheme(openApiProperties));
        }
        return components;
    }

    private SecurityScheme oauth2SecurityScheme(OpenApiProperties openApiProperties) {
        var securityScheme = new SecurityScheme()
                .name(ECA_AUTHENTICATION_SECURITY_SCHEME)
                .type(SecurityScheme.Type.OAUTH2)
                .flows(new OAuthFlows());
        openApiProperties.getApiAuth().forEach(
                (grantType, openApiAuthProperties) -> grantType.handle(new GrantTypeVisitor() {
                    @Override
                    public void visitPassword() {
                        securityScheme.getFlows().setPassword(passwordFlow(openApiAuthProperties));
                    }

                    @Override
                    public void visitClientCredentials() {
                        securityScheme.getFlows().setClientCredentials(clientCredentialsFlow(openApiAuthProperties));
                    }
                }));
        return securityScheme;
    }

    private OAuthFlow passwordFlow(OpenApiAuthProperties openApiAuthProperties) {
        return new OAuthFlow()
                .tokenUrl(openApiAuthProperties.getTokenUrl())
                .refreshUrl(openApiAuthProperties.getRefreshTokenUrl())
                .scopes(scopes(openApiAuthProperties));
    }

    private OAuthFlow clientCredentialsFlow(OpenApiAuthProperties openApiAuthProperties) {
        return new OAuthFlow()
                .tokenUrl(openApiAuthProperties.getTokenUrl())
                .scopes(scopes(openApiAuthProperties));
    }

    private Scopes scopes(OpenApiAuthProperties openApiAuthProperties) {
        var scopes = new Scopes();
        if (!CollectionUtils.isEmpty(openApiAuthProperties.getScopes())) {
            openApiAuthProperties.getScopes().forEach(scope -> scopes.addString(scope, StringUtils.EMPTY));
        }
        return scopes;
    }
}
