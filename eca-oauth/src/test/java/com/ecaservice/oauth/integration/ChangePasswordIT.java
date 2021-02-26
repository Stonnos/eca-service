package com.ecaservice.oauth.integration;

import com.ecaservice.oauth.dto.ChangePasswordRequest;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.service.mail.dictionary.TemplateVariablesDictionary;
import com.ecaservice.oauth.service.mail.dictionary.Templates;
import com.ecaservice.oauth2.test.token.TokenResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

import javax.inject.Inject;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for change password functionality.
 *
 * @author Roman Batygin
 */
class ChangePasswordIT extends AbstractUserIT {

    private static final String NEW_PASSWORD = "newPa66word!";
    private static final String TOKEN_PARAM = "token";

    private static final String API_PREFIX = "/password/change";
    private static final String CHANGE_PASSWORD_REQUEST_URL = "/request";
    private static final String CONFIRM_CHANGE_PASSWORD_REQUEST_URL = "/confirm";

    @Inject
    private PasswordEncoder passwordEncoder;
    @Inject
    private JdbcTokenStore tokenStore;

    @Override
    String getApiPrefix() {
        return API_PREFIX;
    }

    @Test
    void testChangePassword() {
        TokenResponse tokenResponse = obtainOauth2Token();
        createChangePasswordRequest(tokenResponse);
        String token = getHttpRequestParamFromEmailUrlVariable(Templates.CHANGE_PASSWORD,
                TemplateVariablesDictionary.CHANGE_PASSWORD_URL_KEY, TOKEN_PARAM);
        confirmChangePassword(token);
        verifyChangePassword();
    }

    private void createChangePasswordRequest(TokenResponse tokenResponse) {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(getUserPassword(), NEW_PASSWORD);
        getWebClient().post()
                .uri(CHANGE_PASSWORD_REQUEST_URL)
                .header(HttpHeaders.AUTHORIZATION, getAuthorizationHeader(tokenResponse))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(changePasswordRequest)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    private void confirmChangePassword(String token) {
        getWebClient().post()
                .uri(uriBuilder -> uriBuilder.path(CONFIRM_CHANGE_PASSWORD_REQUEST_URL)
                        .queryParam(TOKEN_PARAM, token)
                        .build())
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    private void verifyChangePassword() {
        UserEntity userEntity = getUserEntity();
        UserEntity actualUser = getUserService().getById(userEntity.getId());
        assertThat(actualUser).isNotNull();
        //Verify that password has been changed
        assertThat(passwordEncoder.matches(NEW_PASSWORD, actualUser.getPassword())).isTrue();
        //Verify that all tokens has been revoked
        Collection<OAuth2AccessToken> tokens = tokenStore.findTokensByUserName(actualUser.getLogin());
        assertThat(tokens).isEmpty();
    }
}
