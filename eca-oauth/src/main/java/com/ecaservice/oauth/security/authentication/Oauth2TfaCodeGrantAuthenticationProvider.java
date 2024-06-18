package com.ecaservice.oauth.security.authentication;

import com.ecaservice.oauth.security.Oauth2AccessTokenService;
import com.ecaservice.oauth.security.model.Oauth2TfaCodeAuthenticationToken;
import com.ecaservice.oauth.security.model.TfaCodeAuthenticationRequest;
import com.ecaservice.oauth.service.tfa.TfaCodeService;
import com.ecaservice.oauth.util.Oauth2Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

import static com.ecaservice.oauth.util.Oauth2Utils.getAuthenticatedClientElseThrowInvalidClient;

/**
 * Oauth2 tfa code grant authentication provider.
 *
 * @author Roman Batygin
 */
@Slf4j
@RequiredArgsConstructor
public class Oauth2TfaCodeGrantAuthenticationProvider implements AuthenticationProvider {

    private final TfaCodeService tfaCodeService;
    private final Oauth2AccessTokenService oauth2AccessTokenService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Oauth2TfaCodeAuthenticationToken oauth2TfaCodeAuthenticationToken =
                (Oauth2TfaCodeAuthenticationToken) authentication;
        // Ensure the client is authenticated
        OAuth2ClientAuthenticationToken clientPrincipal =
                getAuthenticatedClientElseThrowInvalidClient(oauth2TfaCodeAuthenticationToken);
        RegisteredClient registeredClient = clientPrincipal.getRegisteredClient();
        // Ensure the client is configured to use this authorization grant type
        if (!registeredClient.getAuthorizationGrantTypes().contains(oauth2TfaCodeAuthenticationToken.getGrantType())) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.UNAUTHORIZED_CLIENT);
        }
        TfaCodeAuthenticationRequest tfaCodeAuthenticationRequest =
                tfaCodeService.consumeAuthorizationCode(oauth2TfaCodeAuthenticationToken.getToken(),
                        oauth2TfaCodeAuthenticationToken.getCode());
        if (!registeredClient.getClientId().equals(tfaCodeAuthenticationRequest.getClientId())) {
            OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST,
                    "Client ID mismatch", StringUtils.EMPTY);
            throw new OAuth2AuthenticationException(error);
        }
        return oauth2AccessTokenService.createAccessToken(oauth2TfaCodeAuthenticationToken,
                tfaCodeAuthenticationRequest.getAuthentication(), clientPrincipal,
                oauth2TfaCodeAuthenticationToken.getGrantType());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return Oauth2TfaCodeAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
