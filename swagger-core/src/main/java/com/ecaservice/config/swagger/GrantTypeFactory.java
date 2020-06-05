package com.ecaservice.config.swagger;

import lombok.experimental.UtilityClass;
import springfox.documentation.service.ResourceOwnerPasswordCredentialsGrant;

/**
 * Oauth2 grant type factory class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class GrantTypeFactory {

    private static final String TOKEN_URL_FORMAT = "%s/oauth/token";

    /**
     * Creates resource owner password credentials grant.
     *
     * @param baseUrl - token base url
     * @return resource owner password credentials grant
     */
    public static ResourceOwnerPasswordCredentialsGrant buildPasswordGrant(String baseUrl) {
        String tokenUrl = String.format(TOKEN_URL_FORMAT, baseUrl);
        return new ResourceOwnerPasswordCredentialsGrant(tokenUrl);
    }
}
