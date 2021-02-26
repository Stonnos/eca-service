package com.ecaservice.oauth.integration;

import com.ecaservice.notification.dto.EmailRequest;
import com.ecaservice.notification.dto.EmailResponse;
import com.ecaservice.oauth.TestHelperUtils;
import com.ecaservice.oauth.dto.CreateUserDto;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.service.UserService;
import com.ecaservice.oauth.service.mail.EmailClient;
import com.ecaservice.oauth2.test.token.TokenResponse;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.Base64Utils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import javax.inject.Inject;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Integration tests for user operations.
 *
 * @author Roman Batygin
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableConfigurationProperties
@TestPropertySource("classpath:application.properties")
abstract class AbstractUserIT {

    private static final String BASE_URL_FORMAT = "http://localhost:%d/%s";
    private static final String BASIC_FORMAT = "Basic %s";
    private static final String BEARER_FORMAT = "Bearer %s";
    private static final String GRANT_TYPE_PARAM = "grant_type";
    private static final String CREDENTIALS_FORMAT = "%s:%s";
    private static final String USERNAME_PARAM = "username";
    private static final String PASSWORD_PARAM = "password";
    private static final String TOKEN_URL = "/oauth/token";

    private static final String PASSWORD = "pa66word!";
    private static final String GRANT_TYPE_PASSWORD = "password";
    private static final String CLIENT_ID = "eca_web";
    private static final String SECRET = "web_secret";

    @LocalServerPort
    private int port;

    @MockBean
    private EmailClient emailClient;
    @Captor
    private ArgumentCaptor<EmailRequest> emailRequestArgumentCaptor;

    @Getter
    @Inject
    private UserService userService;

    @Getter
    private UserEntity userEntity;

    @Getter
    private WebClient webClient;

    @BeforeEach
    void before() {
        clear();
        init();
    }

    @AfterEach
    void after() {
    }

    /**
     * Gets user password in open form.
     *
     * @return user password
     */
    String getUserPassword() {
        return PASSWORD;
    }

    /**
     * Gets api url prefix.
     *
     * @return api url prefix
     */
    abstract String getApiPrefix();

    void init() {
        when(emailClient.sendEmail(any(EmailRequest.class))).thenReturn(new EmailResponse());
        CreateUserDto createUserDto = TestHelperUtils.createUserDto();
        userEntity = userService.createUser(createUserDto, getUserPassword());
        webClient = WebClient.builder()
                .baseUrl(String.format(BASE_URL_FORMAT, port, getApiPrefix()))
                .build();
    }

    void clear() {
    }

    @SneakyThrows
    TokenResponse obtainOauth2Token() {
        WebClient tokenClient = WebClient.builder()
                .baseUrl(String.format(BASE_URL_FORMAT, port, StringUtils.EMPTY))
                .build();
        String credentials = String.format(CREDENTIALS_FORMAT, CLIENT_ID, SECRET);
        String base64Credentials = Base64Utils.encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        TokenResponse tokenResponse = tokenClient.post()
                .uri(uriBuilder -> uriBuilder.path(TOKEN_URL)
                        .queryParam(GRANT_TYPE_PARAM, GRANT_TYPE_PASSWORD)
                        .queryParam(USERNAME_PARAM, userEntity.getLogin())
                        .queryParam(PASSWORD_PARAM, getUserPassword())
                        .build())
                .header(HttpHeaders.AUTHORIZATION, String.format(BASIC_FORMAT, base64Credentials))
                .retrieve()
                .bodyToMono(TokenResponse.class)
                .block();
        assertThat(tokenResponse).isNotNull();
        assertThat(tokenResponse.getAccessToken()).isNotNull();
        return tokenResponse;
    }

    String getAuthorizationHeader(TokenResponse tokenResponse) {
        return String.format(BEARER_FORMAT, tokenResponse.getAccessToken());
    }

    String getHttpRequestParamFromEmailUrlVariable(String template, String variable, String tokenRequestParam) {
        verify(emailClient, atLeastOnce()).sendEmail(emailRequestArgumentCaptor.capture());
        EmailRequest emailRequest = emailRequestArgumentCaptor.getValue();
        assertThat(emailRequest).isNotNull();
        assertThat(emailRequest.getTemplateCode()).isEqualTo(template);
        assertThat(emailRequest.getVariables()).containsKey(variable);
        String resetPasswordUrl = emailRequest.getVariables().get(variable);
        MultiValueMap<String, String> parameters =
                UriComponentsBuilder.fromUriString(resetPasswordUrl).build().getQueryParams();
        String value = parameters.getFirst(tokenRequestParam);
        assertThat(value).isNotNull();
        return value;
    }
}
