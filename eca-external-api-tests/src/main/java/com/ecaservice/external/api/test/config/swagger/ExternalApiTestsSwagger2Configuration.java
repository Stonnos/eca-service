package com.ecaservice.external.api.test.config.swagger;

import com.ecaservice.external.api.test.controller.AutoTestController;
import com.fasterxml.classmate.TypeResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.documentation.spring.web.plugins.Docket;

import javax.inject.Inject;

/**
 * Swagger configuration for external api tests.
 *
 * @author Roman Batygin
 */
@Configuration
@Import(SwaggerBaseConfiguration.class)
public class ExternalApiTestsSwagger2Configuration extends AbstractSwagger2Configuration {

    private static final String EXTERNAL_API_AUTO_TESTS_GROUP = "eca-external-api-tests";

    /**
     * Constructor with spring dependency injection.
     *
     * @param swagger2ApiConfig - swagger api config bean
     */
    @Inject
    public ExternalApiTestsSwagger2Configuration(Swagger2ApiConfig swagger2ApiConfig) {
        super(swagger2ApiConfig);
    }

    @Override
    @Bean(name = "ecaExternalApiTestsDocket")
    public Docket docket(TypeResolver typeResolver) {
        return super.docket(typeResolver);
    }

    @Override
    protected String getGroupName() {
        return EXTERNAL_API_AUTO_TESTS_GROUP;
    }

    @Override
    protected String getControllersPackage() {
        return AutoTestController.class.getPackage().getName();
    }
}
