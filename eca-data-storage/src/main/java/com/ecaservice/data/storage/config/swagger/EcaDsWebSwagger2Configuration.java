package com.ecaservice.data.storage.config.swagger;

import com.ecaservice.config.swagger.AbstractEcaWebSwagger2Configuration;
import com.ecaservice.config.swagger.Swagger2ApiConfig;
import com.ecaservice.config.swagger.SwaggerBaseConfiguration;
import com.fasterxml.classmate.TypeResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.documentation.spring.web.plugins.Docket;

import javax.inject.Inject;

/**
 * Swagger configuration for eca - web.
 *
 * @author Roman Batygin
 */
@Configuration
@Import(SwaggerBaseConfiguration.class)
public class EcaDsWebSwagger2Configuration extends AbstractEcaWebSwagger2Configuration {

    /**
     * Constructor with spring dependency injection.
     *
     * @param swagger2ApiConfig - swagger api config bean
     */
    @Inject
    public EcaDsWebSwagger2Configuration(Swagger2ApiConfig swagger2ApiConfig) {
        super(swagger2ApiConfig);
    }

    @Override
    @Bean(name = "ecaDsDocket")
    public Docket docket(TypeResolver typeResolver) {
        return super.docket(typeResolver);
    }

    @Override
    protected String getControllersPackage() {
        return "";
    }
}
