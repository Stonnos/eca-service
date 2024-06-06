package com.ecaservice.oauth.service;

import com.ecaservice.oauth.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link Oauth2TokenService} class.
 *
 * @author Roman Batygin
 */
@Disabled
@ExtendWith(MockitoExtension.class)
class Oauth2TokenServiceTest {

    private static final String ACCESS_TOKEN = "token";

    private Oauth2TokenService oauth2TokenService;

    private UserEntity userEntity;

    @BeforeEach
    void init() {

    }

    @Test
    void testRevokeTokens() {
        //TODO write tests for revoke token
        oauth2TokenService.revokeTokens(userEntity);
    }
}
