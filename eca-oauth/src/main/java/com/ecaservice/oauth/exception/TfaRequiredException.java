package com.ecaservice.oauth.exception;

import com.ecaservice.oauth.security.model.Oauth2TfaRequiredError;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

/**
 * Two factor authentication required exception.
 *
 * @author Roman Batygin
 */
public class TfaRequiredException extends OAuth2AuthenticationException {

    /**
     * Creates tfa exception.
     *
     * @param oAuth2Error - oauth2 error
     */
    public TfaRequiredException(Oauth2TfaRequiredError oAuth2Error) {
        super(oAuth2Error);
    }
}
