package com.ecaservice.config;

import com.fasterxml.classmate.TypeResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.WildcardType;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Collections;

import static springfox.documentation.schema.AlternateTypeRules.newRule;

/**
 * Configuration for swagger.
 *
 * @author Roman Batygin
 */
@EnableSwagger2
@Configuration
public class Swagger2Configuration {

    private static final String CONTROLLER_PACKAGE = "com.ecaservice.controller";

    /**
     * Returns swagger configuration bean.
     *
     * @return {@link Docket} bean
     */
    @Bean
    @Inject
    public Docket ecaServiceApi(TypeResolver typeResolver, Swagger2ApiConfig swagger2ApiConfig) {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage(CONTROLLER_PACKAGE))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo(swagger2ApiConfig))
                .pathMapping("/")
                .directModelSubstitute(LocalDateTime.class, String.class)
                .alternateTypeRules(
                        newRule(typeResolver.resolve(DeferredResult.class,
                                typeResolver.resolve(ResponseEntity.class, WildcardType.class)),
                                typeResolver.resolve(WildcardType.class))
                );
    }

    @Bean
    protected Swagger2ApiConfig swagger2ApiConfig() {
        return new Swagger2ApiConfig();
    }

    private ApiInfo apiInfo(Swagger2ApiConfig swagger2ApiConfig) {
        return new ApiInfo(swagger2ApiConfig.getTitle(),
                swagger2ApiConfig.getDescription(),
                swagger2ApiConfig.getProjectVersion(), null,
                new Contact(swagger2ApiConfig.getAuthor(), null, swagger2ApiConfig.getEmail()), null, null,
                Collections.emptyList());
    }
}
