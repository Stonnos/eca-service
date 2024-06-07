package com.ecaservice.oauth.security;

import com.ecaservice.oauth.security.model.Oauth2TfaRequiredError;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Oauth2 error parameters converter.
 *
 * @author Roman Batygin
 */
public class OAuth2ErrorParametersConverter implements Converter<OAuth2Error, Map<String, String>> {

    private static final String TOKEN_PARAM_NAME = "token";
    private static final String EXPIRES_IN_PARAM_NAME = "expires_in";

    @Override
    public Map<String, String> convert(OAuth2Error oauth2Error) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put(OAuth2ParameterNames.ERROR, oauth2Error.getErrorCode());
        parameters.put(OAuth2ParameterNames.ERROR_DESCRIPTION, oauth2Error.getDescription());
        if (StringUtils.hasText(oauth2Error.getUri())) {
            parameters.put(OAuth2ParameterNames.ERROR_URI, oauth2Error.getUri());
        }
        if (oauth2Error instanceof Oauth2TfaRequiredError oauth2TfaRequiredError) {
            parameters.put(TOKEN_PARAM_NAME, oauth2TfaRequiredError.getToken());
            parameters.put(EXPIRES_IN_PARAM_NAME, String.valueOf(oauth2TfaRequiredError.getExpiresIn()));
        }
        return parameters;
    }

}