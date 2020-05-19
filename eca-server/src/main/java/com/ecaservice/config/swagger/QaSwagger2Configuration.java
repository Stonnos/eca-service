package com.ecaservice.config.swagger;

import com.ecaservice.controller.qa.QaController;
import com.fasterxml.classmate.TypeResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import springfox.documentation.spring.web.plugins.Docket;

import javax.inject.Inject;

/**
 * Swagger configuration for ECA.
 *
 * @author Roman Batygin
 */
@Profile("!docker-prod")
@Configuration
@Import(SwaggerBaseConfiguration.class)
public class QaSwagger2Configuration extends AbstractSwagger2Configuration {

    private static final String QA_GROUP = "qa";

    /**
     * Constructor with spring dependency injection.
     *
     * @param swagger2ApiConfig - swagger api config bean
     */
    @Inject
    public QaSwagger2Configuration(Swagger2ApiConfig swagger2ApiConfig) {
        super(swagger2ApiConfig);
    }

    @Override
    @Bean(value = "qaDocket")
    public Docket docket(TypeResolver typeResolver) {
        return super.docket(typeResolver);
    }

    @Override
    protected String getGroupName() {
        return QA_GROUP;
    }

    @Override
    protected String getControllersPackage() {
        return QaController.class.getPackage().getName();
    }
}
