package com.ecaservice.oauth.config.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.http.converter.OAuth2ErrorHttpMessageConverter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

/**
 * Oauth2 authentication failure error handler.
 *
 * @author Roman Batygin
 */
@Slf4j
public class OAuth2AuthenticationFailureErrorHandler implements AuthenticationFailureHandler {

    @Getter
    @Setter
    private OAuth2ErrorHttpMessageConverter errorResponseConverter = new OAuth2ErrorHttpMessageConverter();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException authenticationException) throws IOException {
        log.error("Authentication request [{}] error: {}", request.getRequestURI(),
                authenticationException.getMessage());
        ServletServerHttpResponse httpResponse = new ServletServerHttpResponse(response);
        if (authenticationException instanceof OAuth2AuthenticationException) {
            OAuth2Error error = ((OAuth2AuthenticationException) authenticationException).getError();
            if (error instanceof Oauth2TfaRequiredError) {
                // Returns 403 error code for tfa required
                httpResponse.setStatusCode(HttpStatus.FORBIDDEN);
            } else {
                httpResponse.setStatusCode(HttpStatus.BAD_REQUEST);
            }
            this.errorResponseConverter.write(error, MediaType.APPLICATION_JSON, httpResponse);
        } else {
            log.warn("Authentication failure error: expected [{}] exception type",
                    OAuth2AuthenticationException.class.getSimpleName());
        }
    }
}
