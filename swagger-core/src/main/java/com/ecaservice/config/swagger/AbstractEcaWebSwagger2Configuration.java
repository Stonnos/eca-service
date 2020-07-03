package com.ecaservice.config.swagger;

import com.google.common.collect.ImmutableList;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.GrantType;
import springfox.documentation.service.ResourceOwnerPasswordCredentialsGrant;

import java.util.Collections;
import java.util.List;

/**
 * Abstract swagger configuration for eca - web.
 *
 * @author Roman Batygin
 */
public abstract class AbstractEcaWebSwagger2Configuration extends AbstractSwagger2Configuration {

    private static final String TOKEN_URL_FORMAT = "%s/oauth/token";

    private static final String SECURITY_SCHEMA_WEB = "eca-web security";

    private static final List<AuthorizationScope> ECA_WEB_SCOPES =
            ImmutableList.of(new AuthorizationScope("web", "for web operations"));
    private static final String ECA_WEB_GROUP = "eca-web";

    protected AbstractEcaWebSwagger2Configuration(Swagger2ApiConfig swagger2ApiConfig) {
        super(swagger2ApiConfig);
    }

    @Override
    protected String getGroupName() {
        return ECA_WEB_GROUP;
    }

    @Override
    protected List<SecuritySchemeOptions> getSecuritySchemes() {
        String tokenUrl = String.format(TOKEN_URL_FORMAT, getSwagger2ApiConfig().getTokenBaseUrl());
        List<GrantType> grantTypes = Collections.singletonList(new ResourceOwnerPasswordCredentialsGrant(tokenUrl));
        return Collections.singletonList(new SecuritySchemeOptions(SECURITY_SCHEMA_WEB, grantTypes, ECA_WEB_SCOPES));
    }
}
