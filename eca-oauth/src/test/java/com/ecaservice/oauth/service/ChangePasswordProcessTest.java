package com.ecaservice.oauth.service;

import com.ecaservice.oauth.dto.ChangePasswordRequest;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.model.TokenResponse;
import com.ecaservice.oauth.service.mail.dictionary.TemplateVariablesDictionary;
import com.ecaservice.oauth.service.mail.dictionary.Templates;
import com.ecaservice.web.dto.model.ChangePasswordRequestStatusDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for change password functionality.
 *
 * @author Roman Batygin
 */
class ChangePasswordProcessTest extends AbstractUserProcessTest {

    private static final String NEW_PASSWORD = "545NewPa6word!#890";
    private static final String TOKEN_PARAM = "token";
    private static final String CONFIRMATION_CODE_PARAM = "confirmationCode";

    private static final String API_PREFIX = "/password/change";
    private static final String CHANGE_PASSWORD_REQUEST_URL = "/request";
    private static final String CONFIRM_CHANGE_PASSWORD_REQUEST_URL = "/confirm";

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    String getApiPrefix() {
        return API_PREFIX;
    }

    @Test
    void testChangePassword() {
        TokenResponse tokenResponse = obtainOauth2Token();
        var changePasswordRequestStatusDto = createChangePasswordRequest(tokenResponse);
        String confirmationCode =
                getVariableFromEmail(Templates.CHANGE_PASSWORD, TemplateVariablesDictionary.CONFIRMATION_CODE_KEY);
        confirmChangePassword(tokenResponse, changePasswordRequestStatusDto.getToken(), confirmationCode);
        verifyChangePassword();
    }

    private ChangePasswordRequestStatusDto createChangePasswordRequest(TokenResponse tokenResponse) {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(PASSWORD, NEW_PASSWORD);
        return getWebClient().post()
                .uri(CHANGE_PASSWORD_REQUEST_URL)
                .header(HttpHeaders.AUTHORIZATION, getBearerAuthorizationHeader(tokenResponse))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(changePasswordRequest)
                .retrieve()
                .bodyToMono(ChangePasswordRequestStatusDto.class)
                .block();
    }

    private void confirmChangePassword(TokenResponse tokenResponse, String token, String confirmationCode) {
        getWebClient().post()
                .uri(uriBuilder -> uriBuilder.path(CONFIRM_CHANGE_PASSWORD_REQUEST_URL)
                        .queryParam(TOKEN_PARAM, token)
                        .queryParam(CONFIRMATION_CODE_PARAM, confirmationCode)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, getBearerAuthorizationHeader(tokenResponse))
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
        assertThat(countTokens()).isZero();
    }
}
