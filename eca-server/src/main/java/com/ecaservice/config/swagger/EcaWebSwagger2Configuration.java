package com.ecaservice.config.swagger;

import com.ecaservice.controller.web.ExperimentController;
import com.fasterxml.classmate.TypeResolver;
import com.google.common.collect.ImmutableList;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.GrantType;
import springfox.documentation.spring.web.plugins.Docket;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

import static com.ecaservice.config.swagger.GrantTypeFactory.buildPasswordGrant;

/**
 * Swagger configuration for eca - web.
 *
 * @author Roman Batygin
 */
@Configuration
@Import(SwaggerBaseConfiguration.class)
public class EcaWebSwagger2Configuration extends AbstractSwagger2Configuration {

    private static final String SECURITY_SCHEMA_WEB = "eca-web security";

    private static final List<AuthorizationScope> ECA_WEB_SCOPES =
            ImmutableList.of(new AuthorizationScope("web", "for web operations"));
    private static final String ECA_WEB_GROUP = "eca-web";

    /**
     * Constructor with spring dependency injection.
     *
     * @param swagger2ApiConfig - swagger api config bean
     */
    @Inject
    public EcaWebSwagger2Configuration(Swagger2ApiConfig swagger2ApiConfig) {
        super(swagger2ApiConfig);
    }

    @Override
    @Bean(name = "ecaWebDocket")
    public Docket docket(TypeResolver typeResolver) {
        return super.docket(typeResolver);
    }

    @Override
    protected String getGroupName() {
        return ECA_WEB_GROUP;
    }

    @Override
    protected String getControllersPackage() {
        return ExperimentController.class.getPackage().getName();
    }

    @Override
    protected List<SecuritySchemeOptions> getSecuritySchemes() {
        List<GrantType> grantTypes =
                Collections.singletonList(buildPasswordGrant(getSwagger2ApiConfig().getTokenBaseUrl()));
        return Collections.singletonList(new SecuritySchemeOptions(SECURITY_SCHEMA_WEB, grantTypes, ECA_WEB_SCOPES));
    }
}
