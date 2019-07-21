package com.ecaservice.config.swagger;

import com.ecaservice.controller.api.EcaController;
import com.fasterxml.classmate.TypeResolver;
import com.google.common.collect.ImmutableList;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.ClientCredentialsGrant;
import springfox.documentation.service.GrantType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

/**
 * Swagger configuration for ECA.
 *
 * @author Roman Batygin
 */
@Configuration
@EnableSwagger2
@EnableConfigurationProperties(Swagger2ApiConfig.class)
public class EcaSwagger2Configuration extends AbstractSwagger2Configuration {

    private static final String SECURITY_SCHEMA_ECA = "eca security";

    private static final List<AuthorizationScope> ECA_SCOPES =
            ImmutableList.of(new AuthorizationScope("eca", "for eca - desktop"));
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

    @Override
    protected List<SecuritySchemeOptions> getSecuritySchemes() {
        List<GrantType> grantTypes =
                Collections.singletonList(new ClientCredentialsGrant(getSwagger2ApiConfig().getTokenUrl()));
        return Collections.singletonList(new SecuritySchemeOptions(SECURITY_SCHEMA_ECA, grantTypes, ECA_SCOPES));
    }
}
