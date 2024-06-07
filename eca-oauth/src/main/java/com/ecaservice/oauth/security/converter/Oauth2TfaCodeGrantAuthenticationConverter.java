package com.ecaservice.oauth.security.converter;


import com.ecaservice.oauth.security.model.Oauth2TfaCodeAuthenticationToken;
import com.ecaservice.oauth.util.Oauth2Utils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.authentication.AuthenticationConverter;

/**
 * Oauth2 tfa code grant authentication converter.
 *
 * @author Roman Batygin
 */
@Slf4j
public class Oauth2TfaCodeGrantAuthenticationConverter implements AuthenticationConverter {

    private static final String TOKEN_PARAM = "token";
    private static final String TFA_CODE_PARAM = "tfa_code";
    @Override
    public Authentication convert(HttpServletRequest request) {
        // grant_type (REQUIRED)
        String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
        if (!Oauth2Utils.TFA_CODE.getValue().equals(grantType)) {
            return null;
        }
        Authentication clientPrincipal = SecurityContextHolder.getContext().getAuthentication();
        String token = request.getParameter(TOKEN_PARAM);
        // token (REQUIRED)
        if (StringUtils.isEmpty(token)) {
            OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST,
                    "Token must be supplied", StringUtils.EMPTY);
            throw new OAuth2AuthenticationException(error);
        }
        // tfa_code (REQUIRED)
        String tfaCode = request.getParameter(TFA_CODE_PARAM);
        if (StringUtils.isEmpty(tfaCode)) {
            OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST,
                    "Tfa code must be supplied", StringUtils.EMPTY);
            throw new OAuth2AuthenticationException(error);
        }
        return new Oauth2TfaCodeAuthenticationToken(clientPrincipal, token, tfaCode);
    }

}