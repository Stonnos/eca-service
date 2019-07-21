package com.ecaservice.config.swagger;

import com.fasterxml.classmate.TypeResolver;
import com.google.common.collect.ImmutableList;
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
import springfox.documentation.service.ClientCredentialsGrant;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
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
    private static final String RETRIEVE_TOKEN_URL = "%s/oauth/token";

    private static final String SECURITY_SCHEMA_WEB = "eca-web security";
    private static final String SECURITY_SCHEMA_ECA = "eca security";

    private static final List<AuthorizationScope> ECA_WEB_SCOPES =
            ImmutableList.of(new AuthorizationScope("web", "for web operations"));
    private static final List<AuthorizationScope> ECA_SCOPES =
            ImmutableList.of(new AuthorizationScope("eca", "for eca - desktop"));

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
                .securitySchemes(buildSecuritySchemes())
                .securityContexts(buildSecurityContexts())
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

    private SecurityScheme buildSecurityScheme(String name, GrantType grantType, List<AuthorizationScope> scopes) {
        return new OAuthBuilder().name(name).grantTypes(Collections.singletonList(grantType)).scopes(scopes).build();
    }

    private List<SecurityScheme> buildSecuritySchemes() {
        List<SecurityScheme> securitySchemes = newArrayList();
        String tokenUrl = String.format(RETRIEVE_TOKEN_URL, swagger2ApiConfig().getOauthUrl());
        GrantType passwordCredentialsGrant = new ResourceOwnerPasswordCredentialsGrant(tokenUrl);
        securitySchemes.add(buildSecurityScheme(SECURITY_SCHEMA_WEB, passwordCredentialsGrant, ECA_WEB_SCOPES));
        GrantType credentialsGrant = new ClientCredentialsGrant(tokenUrl);
        securitySchemes.add(buildSecurityScheme(SECURITY_SCHEMA_ECA, credentialsGrant, ECA_SCOPES));
        return securitySchemes;
    }

    private SecurityContext buildSecurityContext(String reference, List<AuthorizationScope> scopes) {
        return SecurityContext.builder().securityReferences(Collections.singletonList(
                new SecurityReference(reference, scopes.toArray(new AuthorizationScope[0])))).build();
    }

    private List<SecurityContext> buildSecurityContexts() {
        return Arrays.asList(buildSecurityContext(SECURITY_SCHEMA_WEB, ECA_WEB_SCOPES),
                buildSecurityContext(SECURITY_SCHEMA_ECA, ECA_SCOPES));
    }

    private ApiInfo apiInfo(Swagger2ApiConfig swagger2ApiConfig) {
        return new ApiInfo(swagger2ApiConfig.getTitle(),
                swagger2ApiConfig.getDescription(),
                swagger2ApiConfig.getProjectVersion(), null,
                new Contact(swagger2ApiConfig.getAuthor(), null, swagger2ApiConfig.getEmail()), null, null,
                Collections.emptyList());
    }
}
