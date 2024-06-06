package com.ecaservice.oauth.service;

import com.ecaservice.oauth.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.stereotype.Service;

/**
 * Implements service to manage with oauth2 tokens.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class Oauth2TokenService {

    private final OAuth2AuthorizationService oAuth2AuthorizationService;

    /**
     * Revoke all access and refresh tokens for user.
     *
     * @param userEntity - user entity
     */
    public void revokeTokens(UserEntity userEntity) {
        //TODO impl revoke tokens
        log.info("Starting to revoke all tokens for user: {}", userEntity.getId());
       // Collection<OAuth2AccessToken> oAuth2AccessTokens = tokenStore.findTokensByUserName(userEntity.getLogin());
       // if (!CollectionUtils.isEmpty(oAuth2AccessTokens)) {
        //    log.info("Found [{}] access tokens for user [{}]", oAuth2AccessTokens.size(), userEntity.getId());
       //     oAuth2AccessTokens.forEach(oAuth2AccessToken -> tokenServices.revokeToken(oAuth2AccessToken.getValue()));
      //  }
        log.info("All tokens has been revoked for user: {}", userEntity.getId());
    }
}
