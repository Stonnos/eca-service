package com.ecaservice.oauth.integration;

import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.model.TfaRequiredResponse;
import com.ecaservice.oauth.service.mail.dictionary.TemplateVariablesDictionary;
import com.ecaservice.oauth.service.mail.dictionary.Templates;
import com.ecaservice.oauth2.test.token.TokenResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for two-factor authentication functionality.
 *
 * @author Roman Batygin
 */
class TfaIT extends AbstractUserIT {

    private static final String TOKEN_PARAM = "token";
    private static final String TFA_CODE_PARAM = "tfa_code";
    private static final String TFA_CODE_GRANT_TYPE = "tfa_code";

    private final ObjectMapper objectMapper = new ObjectMapper();

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
    void testTfa() throws JsonProcessingException {
        var tfaRequiredResponse = login();
        assertThat(tfaRequiredResponse).isNotNull();
        String tfaCode = getVariableFromEmail(Templates.TFA_CODE, TemplateVariablesDictionary.TFA_CODE);
        confirmTfaCode(tfaRequiredResponse.getToken(), tfaCode);
    }

    private TfaRequiredResponse login() throws JsonProcessingException {
        try {
            obtainOauth2Token();
        } catch (WebClientResponseException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
            var tfaRequiredResponse = objectMapper.readValue(ex.getResponseBodyAsString(), TfaRequiredResponse.class);
            assertThat(tfaRequiredResponse).isNotNull();
            assertThat(tfaRequiredResponse.getToken()).isNotNull();
            assertThat(tfaRequiredResponse.getExpiresIn()).isNotNull();
            return tfaRequiredResponse;
        }
        return null;
    }

    private void confirmTfaCode(String token, String code) {
        WebClient tokenClient = createWebClient(StringUtils.EMPTY);
        TokenResponse tokenResponse = tokenClient.post()
                .uri(uriBuilder -> uriBuilder.path(TOKEN_URL)
                        .queryParam(GRANT_TYPE_PARAM, TFA_CODE_GRANT_TYPE)
                        .queryParam(TOKEN_PARAM, token)
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
