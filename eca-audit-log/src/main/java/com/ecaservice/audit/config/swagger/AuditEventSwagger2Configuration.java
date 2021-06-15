package com.ecaservice.audit.config.swagger;

import com.ecaservice.audit.controller.AuditEventController;
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
 * Swagger configuration for audit events API.
 *
 * @author Roman Batygin
 */
@Configuration
@Import(SwaggerBaseConfiguration.class)
public class AuditEventSwagger2Configuration extends AbstractSwagger2Configuration {

    private static final String AUDIT_GROUP = "audit";

    /**
     * Constructor with spring dependency injection.
     *
     * @param swagger2ApiConfig - swagger api config bean
     */
    @Inject
    public AuditEventSwagger2Configuration(Swagger2ApiConfig swagger2ApiConfig) {
        super(swagger2ApiConfig);
    }

    @Override
    @Bean(value = "auditEventDocket")
    public Docket docket(TypeResolver typeResolver) {
        return super.docket(typeResolver);
    }

    @Override
    protected String getGroupName() {
        return AUDIT_GROUP;
    }

    @Override
    protected String getControllersPackage() {
        return AuditEventController.class.getPackage().getName();
    }
}
