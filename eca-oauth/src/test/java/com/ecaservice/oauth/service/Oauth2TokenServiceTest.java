package com.ecaservice.oauth.service;

import com.ecaservice.oauth.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

import java.util.Collections;

import static com.ecaservice.oauth.TestHelperUtils.createUserEntity;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link Oauth2TokenService} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(MockitoExtension.class)
class Oauth2TokenServiceTest {

    private static final String ACCESS_TOKEN = "token";

    @Mock
    private JdbcTokenStore tokenStore;
    @Mock
    private DefaultTokenServices tokenServices;
    @InjectMocks
    private Oauth2TokenService oauth2TokenService;

    private UserEntity userEntity;

    @BeforeEach
    void init() {
        OAuth2AccessToken oAuth2AccessToken = mock(OAuth2AccessToken.class);
        when(oAuth2AccessToken.getValue()).thenReturn(ACCESS_TOKEN);
        userEntity = createUserEntity();
        when(tokenStore.findTokensByUserName(userEntity.getLogin())).thenReturn(
                Collections.singletonList(oAuth2AccessToken));
    }

    @Test
    void testRevokeTokens() {
        oauth2TokenService.revokeTokens(userEntity);
        verify(tokenServices, atLeastOnce()).revokeToken(ACCESS_TOKEN);
    }
}
