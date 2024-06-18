package com.ecaservice.oauth.service;

import com.ecaservice.oauth.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
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

    private final JdbcTemplate jdbcTemplate;

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
