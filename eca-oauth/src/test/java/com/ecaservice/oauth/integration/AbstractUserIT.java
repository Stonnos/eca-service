package com.ecaservice.oauth.integration;

import com.ecaservice.notification.dto.EmailRequest;
import com.ecaservice.notification.dto.EmailResponse;
import com.ecaservice.oauth.TestHelperUtils;
import com.ecaservice.oauth.dto.CreateUserDto;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.repository.ChangePasswordRequestRepository;
import com.ecaservice.oauth.repository.ResetPasswordRequestRepository;
import com.ecaservice.oauth.repository.UserEntityRepository;
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
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
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
    private static final String USERNAME_PARAM = "username";
    private static final String PASSWORD_PARAM = "password";
    private final String GRANT_TYPE_PASSWORD = "password";

    static final String TOKEN_URL = "/oauth/token";
    static final String BASIC_FORMAT = "Basic %s";
    static final String BEARER_FORMAT = "Bearer %s";
    static final String GRANT_TYPE_PARAM = "grant_type";
    static final String CREDENTIALS_FORMAT = "%s:%s";
    static final String PASSWORD = "pa66word!";

    @LocalServerPort
    private int port;

    @Value("${oauth2.client.id}")
    private String clientId;

    @Value("${oauth2.client.secret}")
    private String clientSecret;

    @MockBean
    private EmailClient emailClient;
    @Captor
    private ArgumentCaptor<EmailRequest> emailRequestArgumentCaptor;

    @Inject
    @Getter
    private UserEntityRepository userEntityRepository;
    @Inject
    private ResetPasswordRequestRepository resetPasswordRequestRepository;
    @Inject
    private ChangePasswordRequestRepository changePasswordRequestRepository;

    @Getter
    @Inject
    private UserService userService;

    @Getter
    private UserEntity userEntity;

    @Getter
    private WebClient webClient;

    @BeforeEach
    void before() {
        MockitoAnnotations.initMocks(this);
        clear();
        init();
    }

    @AfterEach
    void after() {
        clear();
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
        userEntity = userService.createUser(createUserDto, PASSWORD);
        webClient = createWebClient(getApiPrefix());
    }

    void clear() {
        changePasswordRequestRepository.deleteAll();
        resetPasswordRequestRepository.deleteAll();
        userEntityRepository.deleteAll();
    }

    WebClient createWebClient(String apiPrefix) {
        return WebClient.builder()
                .baseUrl(String.format(BASE_URL_FORMAT, port, apiPrefix))
                .build();
    }

    @SneakyThrows
    TokenResponse obtainOauth2Token() {
        WebClient tokenClient = createWebClient(StringUtils.EMPTY);
        TokenResponse tokenResponse = tokenClient.post()
                .uri(uriBuilder -> uriBuilder.path(TOKEN_URL)
                        .queryParam(GRANT_TYPE_PARAM, GRANT_TYPE_PASSWORD)
                        .queryParam(USERNAME_PARAM, userEntity.getLogin())
                        .queryParam(PASSWORD_PARAM, PASSWORD)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, getBasicAuthorizationHeader())
                .retrieve()
                .bodyToMono(TokenResponse.class)
                .block();
        assertThat(tokenResponse).isNotNull();
        assertThat(tokenResponse.getAccessToken()).isNotNull();
        return tokenResponse;
    }

    String getBasicAuthorizationHeader() {
        String credentials = String.format(CREDENTIALS_FORMAT, clientId, clientSecret);
        String base64Credentials = Base64Utils.encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        return String.format(BASIC_FORMAT, base64Credentials);
    }

    String getBearerAuthorizationHeader(TokenResponse tokenResponse) {
        return String.format(BEARER_FORMAT, tokenResponse.getAccessToken());
    }

    String getHttpRequestParamFromEmailUrlVariable(String template, String variable, String tokenRequestParam) {
        verify(emailClient, atLeastOnce()).sendEmail(emailRequestArgumentCaptor.capture());
        EmailRequest emailRequest = emailRequestArgumentCaptor.getValue();
        assertThat(emailRequest).isNotNull();
        assertThat(emailRequest.getTemplateCode()).isEqualTo(template);
        assertThat(emailRequest.getVariables()).containsKey(variable);
        String url = emailRequest.getVariables().get(variable);
        MultiValueMap<String, String> parameters =
                UriComponentsBuilder.fromUriString(url).build().getQueryParams();
        String value = parameters.getFirst(tokenRequestParam);
        assertThat(value).isNotNull();
        return value;
    }

    String getVariableFromEmail(String template, String variable) {
        verify(emailClient, atLeastOnce()).sendEmail(emailRequestArgumentCaptor.capture());
        EmailRequest emailRequest = emailRequestArgumentCaptor.getValue();
        assertThat(emailRequest).isNotNull();
        assertThat(emailRequest.getTemplateCode()).isEqualTo(template);
        assertThat(emailRequest.getVariables()).containsKey(variable);
        String value = emailRequest.getVariables().get(variable);
        assertThat(value).isNotNull();
        return value;
    }
}
