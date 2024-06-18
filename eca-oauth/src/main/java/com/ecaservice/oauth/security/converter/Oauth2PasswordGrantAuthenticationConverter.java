package com.ecaservice.oauth.security.converter;


import com.ecaservice.oauth.security.model.Oauth2PasswordAuthenticationToken;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
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
        String username = request.getParameter(USERNAME_PARAM);
        if (StringUtils.isEmpty(username)) {
            OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST,
                    "Username must be supplied", StringUtils.EMPTY);
            throw new OAuth2AuthenticationException(error);
        }
        String password = request.getParameter(PASSWORD_PARAM);
        if (StringUtils.isEmpty(password)) {
            OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST,
                    "Password must be supplied", StringUtils.EMPTY);
            throw new OAuth2AuthenticationException(error);
        }
        Authentication clientPrincipal = SecurityContextHolder.getContext().getAuthentication();
        return new Oauth2PasswordAuthenticationToken(clientPrincipal, username, password);
    }

}