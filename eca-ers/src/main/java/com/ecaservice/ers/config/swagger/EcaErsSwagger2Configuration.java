package com.ecaservice.ers.config.swagger;

import com.ecaservice.ers.controller.EvaluationResultsController;
import com.fasterxml.classmate.TypeResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.documentation.spring.web.plugins.Docket;

import javax.inject.Inject;

/**
 * Swagger configuration for eca ers.
 *
 * @author Roman Batygin
 */
@Configuration
@Import(SwaggerBaseConfiguration.class)
public class EcaErsSwagger2Configuration extends AbstractSwagger2Configuration {

    private static final String ECA_ERS_GROUP_NAME = "eca-ers";

    /**
     * Constructor with spring dependency injection.
     *
     * @param swagger2ApiConfig - swagger api config bean
     */
    @Inject
    public EcaErsSwagger2Configuration(Swagger2ApiConfig swagger2ApiConfig) {
        super(swagger2ApiConfig);
    }

    @Override
    @Bean(name = "ecaErsDocket")
    public Docket docket(TypeResolver typeResolver) {
        return super.docket(typeResolver);
    }

    @Override
    protected String getGroupName() {
        return ECA_ERS_GROUP_NAME;
    }

    @Override
    protected String getControllersPackage() {
        return EvaluationResultsController.class.getPackage().getName();
    }
}
