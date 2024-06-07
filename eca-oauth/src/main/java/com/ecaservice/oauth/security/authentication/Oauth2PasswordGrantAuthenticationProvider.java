package com.ecaservice.oauth.security.authentication;

import com.ecaservice.oauth.config.TfaConfig;
import com.ecaservice.oauth.event.model.TfaCodeEmailEvent;
import com.ecaservice.oauth.exception.ChangePasswordRequiredException;
import com.ecaservice.oauth.exception.TfaRequiredException;
import com.ecaservice.oauth.repository.UserEntityRepository;
import com.ecaservice.oauth.security.Oauth2AccessTokenService;
import com.ecaservice.oauth.security.model.Oauth2PasswordAuthenticationToken;
import com.ecaservice.oauth.security.model.Oauth2TfaRequiredError;
import com.ecaservice.oauth.security.model.TfaCodeAuthenticationRequest;
import com.ecaservice.oauth.service.tfa.TfaCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

import static com.ecaservice.oauth.util.Oauth2Utils.getAuthenticatedClientElseThrowInvalidClient;

/**
 * Oauth2 password grant authentication provider.
 *
 * @author Roman Batygin
 */
@Slf4j
@RequiredArgsConstructor
public class Oauth2PasswordGrantAuthenticationProvider implements AuthenticationProvider {

    private final AuthenticationProvider authenticationProvider;
    private final TfaConfig tfaConfig;
    private final TfaCodeService tfaCodeService;
    private final UserEntityRepository userEntityRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final Oauth2AccessTokenService oauth2AccessTokenService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Oauth2PasswordAuthenticationToken oauth2PasswordAuthenticationToken =
                (Oauth2PasswordAuthenticationToken) authentication;
        log.info("Starting to authenticate user [{}]", oauth2PasswordAuthenticationToken.getUsername());
        // Ensure the client is authenticated
        OAuth2ClientAuthenticationToken clientPrincipal =
                getAuthenticatedClientElseThrowInvalidClient(oauth2PasswordAuthenticationToken);
        RegisteredClient registeredClient = clientPrincipal.getRegisteredClient();
        // Ensure the client is configured to use this authorization grant type
        if (!registeredClient.getAuthorizationGrantTypes().contains(oauth2PasswordAuthenticationToken.getGrantType())) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.UNAUTHORIZED_CLIENT);
        }
        Authentication authenticatedUser = authenticateUser(oauth2PasswordAuthenticationToken);
        postAuthenticateUserAdditionalChecks(authenticatedUser, clientPrincipal);
        return oauth2AccessTokenService.createAccessToken(oauth2PasswordAuthenticationToken,
                authenticatedUser, clientPrincipal, oauth2PasswordAuthenticationToken.getGrantType());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return Oauth2PasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private Authentication authenticateUser(Oauth2PasswordAuthenticationToken oauth2PasswordAuthenticationToken) {
        log.info("Starting to authenticate user [{}] by credentials", oauth2PasswordAuthenticationToken.getUsername());
        Authentication usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(oauth2PasswordAuthenticationToken.getUsername(),
                        oauth2PasswordAuthenticationToken.getPassword());
        try {
            Authentication authentication = authenticationProvider.authenticate(usernamePasswordAuthenticationToken);
            log.info("User [{}] has been authenticated by credentials",
                    oauth2PasswordAuthenticationToken.getUsername());
            // Returns UsernamePasswordAuthenticationToken without password in credentials
            return UsernamePasswordAuthenticationToken.authenticated(authentication.getPrincipal(), null,
                    authentication.getAuthorities());
        } catch (BadCredentialsException | AccountStatusException ex) {
            OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST, ex.getMessage(), StringUtils.EMPTY);
            throw new OAuth2AuthenticationException(error);
        }
    }

    private void postAuthenticateUserAdditionalChecks(Authentication authentication,
                                                      OAuth2ClientAuthenticationToken clientPrincipal) {
        var userEntity = userEntityRepository.findUser(authentication.getName());
        if (userEntity.isForceChangePassword()) {
            log.info("Password must be change for user [{}]", userEntity.getLogin());
            throw new ChangePasswordRequiredException();
        }
        if (Boolean.TRUE.equals(tfaConfig.getEnabled()) && userEntity.isTfaEnabled()) {
            log.info("Tfa required for user [{}]. Starting to sent authorization code", userEntity.getLogin());
            String clientId = clientPrincipal.getRegisteredClient().getClientId();
            var tfaCodeAuthenticationRequest = new TfaCodeAuthenticationRequest(clientId, authentication);
            var tfaCodeModel = tfaCodeService.createAuthorizationCode(tfaCodeAuthenticationRequest);
            applicationEventPublisher.publishEvent(
                    new TfaCodeEmailEvent(this, userEntity, tfaCodeModel.getCode()));
            var tfaRequiredError =
                    new Oauth2TfaRequiredError(tfaCodeModel.getToken(), tfaConfig.getCodeValiditySeconds());
            throw new TfaRequiredException(tfaRequiredError);
        }
    }
}
