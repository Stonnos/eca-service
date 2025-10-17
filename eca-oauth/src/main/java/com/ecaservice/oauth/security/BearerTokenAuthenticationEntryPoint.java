package com.ecaservice.oauth.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.http.converter.OAuth2ErrorHttpMessageConverter;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

/**
 * Bearer token authentication entry point.
 *
 * @author Roman Batygin
 */
@Slf4j
public class BearerTokenAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final HttpMessageConverter<OAuth2Error> errorResponseConverter = new OAuth2ErrorHttpMessageConverter();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        log.error("Bearer token authentication request [{}] error: {}", request.getRequestURI(),
                authException.getMessage(), authException);
        ServletServerHttpResponse httpResponse = new ServletServerHttpResponse(response);
        httpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
        if (authException instanceof OAuth2AuthenticationException) {
            OAuth2Error error = ((OAuth2AuthenticationException) authException).getError();
            errorResponseConverter.write(error, MediaType.APPLICATION_JSON, httpResponse);
        }
    }
}