package com.ecaservice.oauth.integration;

import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.service.mail.dictionary.TemplateVariablesDictionary;
import com.ecaservice.oauth.service.mail.dictionary.Templates;
import com.ecaservice.oauth2.test.token.TokenResponse;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * Integration tests for two-factor authentication functionality.
 *
 * @author Roman Batygin
 */
class TfaIT extends AbstractUserIT {

    private static final String TFA_CODE_PARAM = "tfa_code";
    private static final String TFA_CODE_GRANT_TYPE = "tfa_code";

    @Override
    String getApiPrefix() {
        return StringUtils.EMPTY;
    }

    @Override
    void init() {
        super.init();
        //Enable tfa for user
        UserEntity userEntity = getUserEntity();
        userEntity.setTfaEnabled(true);
        getUserEntityRepository().save(userEntity);
    }

    @Test
    void testTfa() {
        login();
        String tfaCode = getVariableFromEmail(Templates.TFA_CODE, TemplateVariablesDictionary.TFA_CODE);
        confirmTfaCode(tfaCode);
    }

    private void login() {
        try {
            obtainOauth2Token();
        } catch (WebClientResponseException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
            return;
        }
        fail("Expected 403 http error code while login via tfa");
    }

    private void confirmTfaCode(String code) {
        WebClient tokenClient = createWebClient(StringUtils.EMPTY);
        TokenResponse tokenResponse = tokenClient.post()
                .uri(uriBuilder -> uriBuilder.path(TOKEN_URL)
                        .queryParam(GRANT_TYPE_PARAM, TFA_CODE_GRANT_TYPE)
                        .queryParam(TFA_CODE_PARAM, code)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, getBasicAuthorizationHeader())
                .retrieve()
                .bodyToMono(TokenResponse.class)
                .block();
        assertThat(tokenResponse).isNotNull();
        assertThat(tokenResponse.getAccessToken()).isNotNull();
    }
}
