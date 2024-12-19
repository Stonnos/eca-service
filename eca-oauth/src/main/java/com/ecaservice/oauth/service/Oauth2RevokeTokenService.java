package com.ecaservice.oauth.service;

import com.ecaservice.oauth.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.stereotype.Service;

/**
 * Implements service to revoke oauth2 tokens.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class Oauth2RevokeTokenService {

    private static final String TABLE_NAME = "oauth2_authorization";
    private static final String REVOKE_TOKENS_QUERY = "delete from %s where principal_name = ?";

    private static final String REVOKE_TOKEN_QUERY =
            "delete from %s where principal_name = ? and access_token_value = ?";

    private final JdbcTemplate jdbcTemplate;

    /**
     * Revoke access and refresh tokens for user.
     *
     * @param authentication - bearer token authentication
     */
    public void revokeToken(BearerTokenAuthentication authentication) {
        log.info("Starting to revoke access/refresh token for user: {}", authentication.getName());
        String query = String.format(REVOKE_TOKEN_QUERY, TABLE_NAME);
        jdbcTemplate.update(query, authentication.getName(), authentication.getToken().getTokenValue());
        log.info("Access/refresh token has been revoked for user: {}", authentication.getName());
    }

    /**
     * Revoke all access and refresh tokens for user.
     *
     * @param userEntity - user entity
     */
    public void revokeTokens(UserEntity userEntity) {
        log.info("Starting to revoke all tokens for user: {}", userEntity.getId());
        String query = String.format(REVOKE_TOKENS_QUERY, TABLE_NAME);
        int deleted = jdbcTemplate.update(query, userEntity.getLogin());
        log.info("[{}] tokens has been revoked for user: {}", deleted, userEntity.getId());
    }
}
