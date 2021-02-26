package com.ecaservice.oauth.service;

import com.ecaservice.oauth.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;

/**
 * Implements service to manage with oauth2 tokens.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class Oauth2TokenService {

    private final JdbcTokenStore tokenStore;
    private final DefaultTokenServices tokenServices;

    /**
     * Revoke all access and refresh tokens for user.
     *
     * @param userEntity - user entity
     */
    public void revokeTokens(UserEntity userEntity) {
        log.info("Starting to revoke all tokens for user: {}", userEntity.getId());
        Collection<OAuth2AccessToken> oAuth2AccessTokens = tokenStore.findTokensByUserName(userEntity.getLogin());
        if (!CollectionUtils.isEmpty(oAuth2AccessTokens)) {
            log.info("Found [{}] access tokens for user [{}]", oAuth2AccessTokens.size(), userEntity.getId());
            oAuth2AccessTokens.forEach(oAuth2AccessToken -> tokenServices.revokeToken(oAuth2AccessToken.getValue()));
        }
        log.info("All tokens has been revoked for user: {}", userEntity.getId());
    }
}
