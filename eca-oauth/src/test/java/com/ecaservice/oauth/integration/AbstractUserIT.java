package com.ecaservice.oauth.integration;

import com.ecaservice.notification.dto.EmailRequest;
import com.ecaservice.notification.dto.EmailResponse;
import com.ecaservice.oauth.TestHelperUtils;
import com.ecaservice.oauth.dto.CreateUserDto;
import com.ecaservice.oauth.dto.ForgotPasswordRequest;
import com.ecaservice.oauth.dto.ResetPasswordRequest;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.service.UserService;
import com.ecaservice.oauth.service.mail.EmailClient;
import com.ecaservice.oauth.service.mail.dictionary.TemplateVariablesDictionary;
import com.ecaservice.oauth.service.mail.dictionary.Templates;
import lombok.Getter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import javax.inject.Inject;
import java.util.Collection;

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
    private static final String PASSWORD = "pa66word!";

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

    abstract String getApiPrefix();

    void init() {
        when(emailClient.sendEmail(any(EmailRequest.class))).thenReturn(new EmailResponse());
        CreateUserDto createUserDto = TestHelperUtils.createUserDto();
        userEntity = userService.createUser(createUserDto, PASSWORD);
        webClient = WebClient.builder()
                .baseUrl(String.format(BASE_URL_FORMAT, port, getApiPrefix()))
                .build();
    }

    void clear() {
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
