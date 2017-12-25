package com.ecaservice.config;

import com.fasterxml.classmate.TypeResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import java.time.OffsetDateTime;
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

    @Autowired
    private TypeResolver typeResolver;

    /**
     * Returns swagger configuration bean.
     *
     * @return {@link Docket} bean
     */
    @Bean
    public Docket ecaServiceApi(@Value("${project.version}") String projectVersion) {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.ecaservice.controller"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo(projectVersion))
                .pathMapping("/")
                .directModelSubstitute(OffsetDateTime.class, String.class)
                .alternateTypeRules(
                        newRule(typeResolver.resolve(DeferredResult.class,
                                typeResolver.resolve(ResponseEntity.class, WildcardType.class)),
                                typeResolver.resolve(WildcardType.class))
                );
    }

    private ApiInfo apiInfo(String projectVersion) {
        return new ApiInfo("Eca service REST API",
                "API for individual and ensemble classification models learning",
                projectVersion, null,
                new Contact("Roman Batygin", null, "roman.batygin@mail.ru"), null, null,
                Collections.emptyList());
    }
}
