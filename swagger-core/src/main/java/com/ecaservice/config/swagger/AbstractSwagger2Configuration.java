package com.ecaservice.config.swagger;

import com.fasterxml.classmate.TypeResolver;
import lombok.Getter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;
import springfox.documentation.builders.OAuthBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.WildcardType;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static springfox.documentation.schema.AlternateTypeRules.newRule;

/**
 * Abstract swagger configuration.
 *
 * @author Roman Batygin
 */
public abstract class AbstractSwagger2Configuration {

    @Getter
    private final Swagger2ApiConfig swagger2ApiConfig;

    protected AbstractSwagger2Configuration(Swagger2ApiConfig swagger2ApiConfig) {
        this.swagger2ApiConfig = swagger2ApiConfig;
    }

    /**
     * Returns swagger configuration bean.
     *
     * @return docket bean
     */
    public Docket docket(TypeResolver typeResolver) {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName(getGroupName())
                .select()
                .apis(RequestHandlerSelectors.basePackage(getControllersPackage()))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo())
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

    /**
     * Gets docket group name.
     *
     * @return docket group name
     */
    protected abstract String getGroupName();

    /**
     * Gets controller base package name.
     *
     * @return controller base package name
     */
    protected abstract String getControllersPackage();

    protected List<SecuritySchemeOptions> getSecuritySchemes() {
        return Collections.emptyList();
    }

    private SecurityScheme buildSecurityScheme(SecuritySchemeOptions securitySchemeOptions) {
        return new OAuthBuilder()
                .name(securitySchemeOptions.getName())
                .grantTypes(securitySchemeOptions.getGrantTypes())
                .scopes(securitySchemeOptions.getScopes()).build();
    }

    private SecurityContext buildSecurityContext(SecuritySchemeOptions securitySchemeOptions) {
        return SecurityContext.builder().securityReferences(Collections.singletonList(
                new SecurityReference(securitySchemeOptions.getName(),
                        securitySchemeOptions.getScopes().toArray(new AuthorizationScope[0])))).build();
    }

    private List<SecurityScheme> buildSecuritySchemes() {
        return getSecuritySchemes().stream().map(this::buildSecurityScheme).collect(Collectors.toList());
    }

    private List<SecurityContext> buildSecurityContexts() {
        return getSecuritySchemes().stream().map(this::buildSecurityContext).collect(Collectors.toList());
    }

    private ApiInfo apiInfo() {
        SwaggerApiInfo apiInfo = swagger2ApiConfig.getGroups().get(getGroupName());
        return new ApiInfo(apiInfo.getTitle(),
                apiInfo.getDescription(),
                swagger2ApiConfig.getProjectVersion(), null,
                new Contact(apiInfo.getAuthor(), null, apiInfo.getEmail()), null, null,
                Collections.emptyList());
    }
}
