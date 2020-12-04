package com.ecaservice.external.api.config.swagger;

import com.ecaservice.config.swagger.AbstractSwagger2Configuration;
import com.ecaservice.config.swagger.SecuritySchemeOptions;
import com.ecaservice.config.swagger.Swagger2ApiConfig;
import com.ecaservice.config.swagger.SwaggerBaseConfiguration;
import com.ecaservice.external.api.controller.ExternalApiController;
import com.fasterxml.classmate.TypeResolver;
import com.google.common.collect.ImmutableList;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.ClientCredentialsGrant;
import springfox.documentation.service.GrantType;
import springfox.documentation.spring.web.plugins.Docket;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

/**
 * Swagger configuration for eca external API.
 *
 * @author Roman Batygin
 */
@Configuration
@Import(SwaggerBaseConfiguration.class)
public class ExternalApiSwagger2Configuration extends AbstractSwagger2Configuration {

    private static final String TOKEN_URL_FORMAT = "%s/oauth/token";

    private static final String ECA_EXTERNAL_API_GROUP = "eca-external-api";
    private static final String SECURITY_SCHEMA = "eca-external-api security";
    private static final List<AuthorizationScope> AUTHORIZATION_SCOPES =
            ImmutableList.of(new AuthorizationScope("external-api", "for external api operations"));

    /**
     * Constructor with spring dependency injection.
     *
     * @param swagger2ApiConfig - swagger api config bean
     */
    @Inject
    public ExternalApiSwagger2Configuration(Swagger2ApiConfig swagger2ApiConfig) {
        super(swagger2ApiConfig);
    }

    @Override
    @Bean(name = "ecaExternalApiDocket")
    public Docket docket(TypeResolver typeResolver) {
        return super.docket(typeResolver);
    }

    @Override
    protected String getGroupName() {
        return ECA_EXTERNAL_API_GROUP;
    }

    @Override
    protected String getControllersPackage() {
        return ExternalApiController.class.getPackage().getName();
    }

    @Override
    protected List<SecuritySchemeOptions> getSecuritySchemes() {
        String tokenUrl = String.format(TOKEN_URL_FORMAT, getSwagger2ApiConfig().getTokenBaseUrl());
        List<GrantType> grantTypes = Collections.singletonList(new ClientCredentialsGrant(tokenUrl));
        return Collections.singletonList(new SecuritySchemeOptions(SECURITY_SCHEMA, grantTypes, AUTHORIZATION_SCOPES));
    }
}
