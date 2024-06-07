package com.ecaservice.oauth.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
 * Oauth2 access token service.
 *
 * @author Roman Batygin
 */
@Slf4j
@RequiredArgsConstructor
public class Oauth2AccessTokenService {

    private final OAuth2AuthorizationService authorizationService;
    private final OAuth2AccessTokenGenerator tokenGenerator;
    private final OAuth2RefreshTokenGenerator refreshTokenGenerator;

    /**
     * Creates access token.
     *
     * @param authenticationGrant - authentication grant object
     * @param authenticatedUser   - authenticated user
     * @param clientPrincipal     - client principal
     * @param grantType           - grant type
     * @return authentication object
     * @throws AuthenticationException in case of authentication error
     */
    public Authentication createAccessToken(Authentication authenticationGrant,
                                            Authentication authenticatedUser,
                                            OAuth2ClientAuthenticationToken clientPrincipal,
                                            AuthorizationGrantType grantType) throws AuthenticationException {
        RegisteredClient registeredClient = clientPrincipal.getRegisteredClient();
        // Generate the access token
        DefaultOAuth2TokenContext.Builder tokenContextBuilder = DefaultOAuth2TokenContext.builder()
                .registeredClient(registeredClient)
                .principal(authenticatedUser)
                .authorizationServerContext(AuthorizationServerContextHolder.getContext())
                .tokenType(OAuth2TokenType.ACCESS_TOKEN)
                .authorizationGrantType(grantType)
                .authorizedScopes(registeredClient.getScopes())
                .authorizationGrant(authenticationGrant);

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
                .authorizationGrantType(grantType);
        if (generatedAccessToken instanceof ClaimAccessor) {
            var claims = ((ClaimAccessor) generatedAccessToken).getClaims();
            authorizationBuilder.token(accessToken,
                    (metadata) -> metadata.put(OAuth2Authorization.Token.CLAIMS_METADATA_NAME, claims));
            log.info("Claims {} has been set for user [{}] access token", authenticatedUser.getName(), claims);
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
}
