package com.ecaservice.external.api.test.config.swagger;

import com.ecaservice.config.swagger.AbstractSwagger2Configuration;
import com.ecaservice.config.swagger.Swagger2ApiConfig;
import com.ecaservice.config.swagger.SwaggerBaseConfiguration;
import com.fasterxml.classmate.TypeResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.documentation.spring.web.plugins.Docket;

import javax.inject.Inject;

/**
 * Swagger configuration for eca load tests.
 *
 * @author Roman Batygin
 */
@Configuration
@Import(SwaggerBaseConfiguration.class)
public class ExternalApiTestsSwagger2Configuration extends AbstractSwagger2Configuration {

    private static final String ECA_LOAD_TESTS_GROUP = "eca-external-api-tests";

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
        return ECA_LOAD_TESTS_GROUP;
    }

    @Override
    protected String getControllersPackage() {
        return null;
    }
}
