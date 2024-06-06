package com.ecaservice.oauth.config.security;


import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.authentication.AuthenticationConverter;

/**
 * Oauth2 password grant authentication converter.
 *
 * @author Roman Batygin
 */
@Slf4j
public class Oauth2PasswordGrantAuthenticationConverter implements AuthenticationConverter {

    private static final String USERNAME_PARAM = "username";
    private static final String PASSWORD_PARAM = "password";

    @Override
    public Authentication convert(HttpServletRequest request) {
        // grant_type (REQUIRED)
        String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
        if (!AuthorizationGrantType.PASSWORD.getValue().equals(grantType)) {
            return null;
        }
        Authentication clientPrincipal = SecurityContextHolder.getContext().getAuthentication();
        return new Oauth2PasswordAuthenticationToken(clientPrincipal, request.getParameter(USERNAME_PARAM),
                request.getParameter(PASSWORD_PARAM));
    }

}