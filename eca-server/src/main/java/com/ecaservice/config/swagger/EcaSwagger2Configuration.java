package com.ecaservice.config.swagger;

import com.ecaservice.controller.api.EcaController;
import com.fasterxml.classmate.TypeResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.documentation.spring.web.plugins.Docket;

import javax.inject.Inject;

/**
 * Swagger configuration for ECA.
 *
 * @author Roman Batygin
 */
@Configuration
@Import(SwaggerBaseConfiguration.class)
public class EcaSwagger2Configuration extends AbstractSwagger2Configuration {

    private static final String ECA_GROUP = "eca";

    /**
     * Constructor with spring dependency injection.
     *
     * @param swagger2ApiConfig - swagger api config bean
     */
    @Inject
    public EcaSwagger2Configuration(Swagger2ApiConfig swagger2ApiConfig) {
        super(swagger2ApiConfig);
    }

    @Override
    @Bean(value = "ecaDocket")
    public Docket docket(TypeResolver typeResolver) {
        return super.docket(typeResolver);
    }

    @Override
    protected String getGroupName() {
        return ECA_GROUP;
    }

    @Override
    protected String getControllersPackage() {
        return EcaController.class.getPackage().getName();
    }
}
