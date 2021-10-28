package com.ecaservice.oauth.integration;

import com.ecaservice.oauth.dto.CreateResetPasswordRequest;
import com.ecaservice.oauth.dto.ResetPasswordRequest;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.service.mail.dictionary.TemplateVariablesDictionary;
import com.ecaservice.oauth.service.mail.dictionary.Templates;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

import javax.inject.Inject;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for reset password functionality.
 *
 * @author Roman Batygin
 */
class ResetPasswordIT extends AbstractUserIT {

    private static final String NEW_PASSWORD = "newPa66word!";
    private static final String TOKEN_PARAM = "token";

    private static final String API_PREFIX = "/password";
    private static final String FORGOT_URL = "/forgot";
    private static final String RESET_URL = "/reset";

    @Inject
    private PasswordEncoder passwordEncoder;
    @Inject
    private JdbcTokenStore tokenStore;

    @Override
    String getApiPrefix() {
        return API_PREFIX;
    }

    @Test
    void testResetPassword() {
        createResetPasswordRequest();
        String token = getHttpRequestParamFromEmailUrlVariable(Templates.RESET_PASSWORD,
                TemplateVariablesDictionary.RESET_PASSWORD_URL_KEY, TOKEN_PARAM);
        resetPassword(token);
        verifyResetPassword();
    }

    private void createResetPasswordRequest() {
        var createResetPasswordRequest = new CreateResetPasswordRequest(getUserEntity().getEmail());
        getWebClient().post()
                .uri(FORGOT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createResetPasswordRequest)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    private void resetPassword(String token) {
        ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest(token, NEW_PASSWORD);
        getWebClient().post()
                .uri(RESET_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(resetPasswordRequest)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    private void verifyResetPassword() {
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
