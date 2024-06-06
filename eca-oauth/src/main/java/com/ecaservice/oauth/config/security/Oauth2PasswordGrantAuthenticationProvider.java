package com.ecaservice.oauth.config.security;

import com.ecaservice.oauth.config.TfaConfig;
import com.ecaservice.oauth.event.model.TfaCodeEmailEvent;
import com.ecaservice.oauth.exception.ChangePasswordRequiredException;
import com.ecaservice.oauth.exception.TfaRequiredException;
import com.ecaservice.oauth.repository.UserEntityRepository;
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
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClaimAccessor;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContextHolder;
import org.springframework.security.oauth2.server.authorization.token.DefaultOAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2AccessTokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2RefreshTokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;

import java.security.Principal;

/**
 * Oauth2 password grant authentication provider.
 *
 * @author Roman Batygin
 */
@Slf4j
@RequiredArgsConstructor
public class Oauth2PasswordGrantAuthenticationProvider implements AuthenticationProvider {

    private final OAuth2AuthorizationService authorizationService;
    private final AuthenticationProvider authenticationProvider;
    private final OAuth2AccessTokenGenerator tokenGenerator;
    private final OAuth2RefreshTokenGenerator refreshTokenGenerator;
    private final TfaConfig tfaConfig;
    private final TfaCodeService tfaCodeService;
    private final UserEntityRepository userEntityRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

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
        if (!registeredClient.getAuthorizationGrantTypes().contains(AuthorizationGrantType.PASSWORD)) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.UNAUTHORIZED_CLIENT);
        }
        Authentication authenticatedUser = authenticateUser(oauth2PasswordAuthenticationToken);
        postAuthenticateUserAdditionalChecks(authenticatedUser);
        // Generate the access token
        DefaultOAuth2TokenContext.Builder tokenContextBuilder = DefaultOAuth2TokenContext.builder()
                .registeredClient(registeredClient)
                .principal(authenticatedUser)
                .authorizationServerContext(AuthorizationServerContextHolder.getContext())
                .tokenType(OAuth2TokenType.ACCESS_TOKEN)
                .authorizationGrantType(AuthorizationGrantType.PASSWORD)
                .authorizedScopes(registeredClient.getScopes())
                .authorizationGrant(oauth2PasswordAuthenticationToken);

        OAuth2TokenContext tokenContext = tokenContextBuilder.build();

        log.info("Starting to generate access token for user [{}]", authenticatedUser.getName());
        OAuth2Token generatedAccessToken = this.tokenGenerator.generate(tokenContext);
        if (generatedAccessToken == null) {
            OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR,
                    "The token generator failed to generate the access token.", null);
            throw new OAuth2AuthenticationException(error);
        }
        OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER,
                generatedAccessToken.getTokenValue(), generatedAccessToken.getIssuedAt(),
                generatedAccessToken.getExpiresAt(), tokenContext.getAuthorizedScopes());
        log.info("Access token has been generated for user [{}]", authenticatedUser.getName());

        // Initialize the OAuth2Authorization
        OAuth2Authorization.Builder authorizationBuilder = OAuth2Authorization.withRegisteredClient(registeredClient)
                .principalName(authenticatedUser.getName())
                .attribute(Principal.class.getName(), authenticatedUser)
                .authorizedScopes(registeredClient.getScopes())
                .authorizationGrantType(AuthorizationGrantType.PASSWORD);
        if (generatedAccessToken instanceof ClaimAccessor) {
            var claims = ((ClaimAccessor) generatedAccessToken).getClaims();
            authorizationBuilder.token(accessToken,
                    (metadata) -> metadata.put(OAuth2Authorization.Token.CLAIMS_METADATA_NAME, claims));
            log.info("Claims {} has been set for user [{}] access token",
                    authenticatedUser.getName(), claims);
        } else {
            authorizationBuilder.accessToken(accessToken);
        }

        // Generates refresh token
        OAuth2RefreshToken refreshToken = null;
        if (registeredClient.getAuthorizationGrantTypes().contains(AuthorizationGrantType.REFRESH_TOKEN)) {
            log.info("Starting to generate refresh token for user [{}]", authenticatedUser.getName());
            tokenContext = tokenContextBuilder.tokenType(OAuth2TokenType.REFRESH_TOKEN).build();
            refreshToken = this.refreshTokenGenerator.generate(tokenContext);
            if (refreshToken == null) {
                OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR,
                        "The token generator failed to generate a valid refresh token.", null);
                throw new OAuth2AuthenticationException(error);
            }
            authorizationBuilder.refreshToken(refreshToken);
            log.info("Refresh token has been generated for user [{}]", authenticatedUser.getName());
        }

        OAuth2Authorization authorization = authorizationBuilder.build();
        // Save the OAuth2Authorization
        log.info("Starting to save the OAuth2Authorization for user [{}]", authenticatedUser.getName());
        this.authorizationService.save(authorization);
        log.info("OAuth2Authorization has been saved for user [{}] with: scopes [{}], claims {}",
                authenticatedUser.getName(), authorization.getAccessToken().getToken().getScopes(),
                authorization.getAccessToken().getClaims());
        var oAuth2AccessTokenAuthenticationToken =
                new OAuth2AccessTokenAuthenticationToken(registeredClient, authenticatedUser, accessToken,
                        refreshToken);
        log.info("User [{}] has been authenticated", authenticatedUser.getName());
        return oAuth2AccessTokenAuthenticationToken;
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
            return authentication;
        } catch (BadCredentialsException | AccountStatusException ex) {
            OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST, ex.getMessage(), StringUtils.EMPTY);
            throw new OAuth2AuthenticationException(error);
        }
    }

    private void postAuthenticateUserAdditionalChecks(Authentication authentication) {
        var userEntity = userEntityRepository.findUser(authentication.getName());
        if (userEntity.isForceChangePassword()) {
            log.info("Password must be change for user [{}]", userEntity.getLogin());
            throw new ChangePasswordRequiredException();
        }
        if (Boolean.TRUE.equals(tfaConfig.getEnabled()) && userEntity.isTfaEnabled()) {
            log.info("Tfa required for user [{}]. Starting to sent authorization code", userEntity.getLogin());
            var tfaCodeModel = tfaCodeService.createAuthorizationCode(authentication);
            applicationEventPublisher.publishEvent(
                    new TfaCodeEmailEvent(this, userEntity, tfaCodeModel.getCode()));
            var tfaRequiredError =
                    new Oauth2TfaRequiredError(tfaCodeModel.getToken(), tfaConfig.getCodeValiditySeconds());
            throw new TfaRequiredException(tfaRequiredError);
        }
    }

    private OAuth2ClientAuthenticationToken getAuthenticatedClientElseThrowInvalidClient(
            Authentication authentication) {
        OAuth2ClientAuthenticationToken clientPrincipal = null;
        if (OAuth2ClientAuthenticationToken.class.isAssignableFrom(authentication.getPrincipal().getClass())) {
            clientPrincipal = (OAuth2ClientAuthenticationToken) authentication.getPrincipal();
        }
        if (clientPrincipal != null && clientPrincipal.isAuthenticated()) {
            return clientPrincipal;
        }
        throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_CLIENT);
    }

}
