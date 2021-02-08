package com.ecaservice.mail.config.swagger;

import com.ecaservice.config.swagger.AbstractSwagger2Configuration;
import com.ecaservice.config.swagger.Swagger2ApiConfig;
import com.ecaservice.config.swagger.SwaggerBaseConfiguration;
import com.ecaservice.mail.controller.EmailController;
import com.fasterxml.classmate.TypeResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.documentation.spring.web.plugins.Docket;

import javax.inject.Inject;

/**
 * Swagger configuration for eca mail.
 *
 * @author Roman Batygin
 */
@Configuration
@Import(SwaggerBaseConfiguration.class)
public class EcaMailSwagger2Configuration extends AbstractSwagger2Configuration {

    private static final String ECA_MAIL_GROUP_NAME = "eca-mail";

    /**
     * Constructor with spring dependency injection.
     *
     * @param swagger2ApiConfig - swagger api config bean
     */
    @Inject
    public EcaMailSwagger2Configuration(Swagger2ApiConfig swagger2ApiConfig) {
        super(swagger2ApiConfig);
    }

    @Override
    @Bean(name = "ecaMailDocket")
    public Docket docket(TypeResolver typeResolver) {
        return super.docket(typeResolver);
    }

    @Override
    protected String getGroupName() {
        return ECA_MAIL_GROUP_NAME;
    }

    @Override
    protected String getControllersPackage() {
        return EmailController.class.getPackage().getName();
    }
}
