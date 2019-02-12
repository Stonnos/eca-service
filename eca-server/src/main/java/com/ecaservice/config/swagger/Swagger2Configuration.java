package com.ecaservice.config.swagger;

import com.fasterxml.classmate.TypeResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;
import springfox.documentation.builders.OAuthBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.WildcardType;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.GrantType;
import springfox.documentation.service.ResourceOwnerPasswordCredentialsGrant;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SecurityConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static springfox.documentation.schema.AlternateTypeRules.newRule;

/**
 * Configuration for swagger.
 *
 * @author Roman Batygin
 */
@Configuration
@EnableSwagger2
public class Swagger2Configuration {

    private static final String CONTROLLER_PACKAGE = "com.ecaservice.controller";
    private static final String SECURITY_SCHEMA_OAUTH2 = "spring_oauth";
    private static final String RETRIEVE_TOKEN_URL = "%s/oauth/token";
    private static final String SCOPE = "web";
    private static final String SCOPE_DESCRIPTION = "for web operations";

    /**
     * Returns swagger configuration bean.
     *
     * @return {@link Docket} bean
     */
    @Bean
    public Docket ecaServiceApi(TypeResolver typeResolver, Swagger2ApiConfig swagger2ApiConfig) {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage(CONTROLLER_PACKAGE))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo(swagger2ApiConfig))
                .securitySchemes(Collections.singletonList(buildSecurityScheme()))
                .securityContexts(Collections.singletonList(buildSecurityContext()))
                .pathMapping("/")
                .directModelSubstitute(LocalDateTime.class, String.class)
                .alternateTypeRules(
                        newRule(typeResolver.resolve(DeferredResult.class,
                                typeResolver.resolve(ResponseEntity.class, WildcardType.class)),
                                typeResolver.resolve(WildcardType.class))
                );
    }

    @Bean
    public SecurityConfiguration security() {
        return SecurityConfigurationBuilder.builder()
                .clientId(swagger2ApiConfig().getClientId())
                .clientSecret(swagger2ApiConfig().getSecret())
                .useBasicAuthenticationWithAccessCodeGrant(false)
                .build();
    }

    @Bean
    public Swagger2ApiConfig swagger2ApiConfig() {
        return new Swagger2ApiConfig();
    }

    private SecurityScheme buildSecurityScheme() {
        GrantType grantType = new ResourceOwnerPasswordCredentialsGrant(String.format(RETRIEVE_TOKEN_URL,
                swagger2ApiConfig().getOauthUrl()));
        return new OAuthBuilder().name(SECURITY_SCHEMA_OAUTH2).grantTypes(Collections.singletonList(grantType)).scopes(
                getScopes()).build();
    }

    private List<AuthorizationScope> getScopes() {
        return Collections.singletonList(new AuthorizationScope(SCOPE, SCOPE_DESCRIPTION));
    }

    private SecurityContext buildSecurityContext() {
        return SecurityContext.builder().securityReferences(Collections.singletonList(
                new SecurityReference(SECURITY_SCHEMA_OAUTH2, getScopes().toArray(new AuthorizationScope[0])))).build();
    }

    private ApiInfo apiInfo(Swagger2ApiConfig swagger2ApiConfig) {
        return new ApiInfo(swagger2ApiConfig.getTitle(),
                swagger2ApiConfig.getDescription(),
                swagger2ApiConfig.getProjectVersion(), null,
                new Contact(swagger2ApiConfig.getAuthor(), null, swagger2ApiConfig.getEmail()), null, null,
                Collections.emptyList());
    }
}
