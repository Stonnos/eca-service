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
 * Integration tests for reset password functionality.
 *
 * @author Roman Batygin
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableConfigurationProperties
@TestPropertySource("classpath:application.properties")
class ResetPasswordIT {

    private static final String BASE_URL_FORMAT = "http://localhost:%d/password";
    private static final String NEW_PASSWORD = "newPa66word!";
    private static final String PASSWORD = "pa66word!";
    private static final String TOKEN_PARAM = "token";

    private static final String FORGOT_URL = "/forgot";
    private static final String RESET_URL = "/reset";

    @LocalServerPort
    private int port;

    @MockBean
    private EmailClient emailClient;
    @Captor
    private ArgumentCaptor<EmailRequest> emailRequestArgumentCaptor;

    @Inject
    private UserService userService;
    @Inject
    private PasswordEncoder passwordEncoder;
    @Inject
    private JdbcTokenStore tokenStore;

    private UserEntity userEntity;

    private WebClient webClient;

    @BeforeEach
    void init() {
        when(emailClient.sendEmail(any(EmailRequest.class))).thenReturn(new EmailResponse());
        CreateUserDto createUserDto = TestHelperUtils.createUserDto();
        userEntity = userService.createUser(createUserDto, PASSWORD);
        webClient = WebClient.builder()
                .baseUrl(String.format(BASE_URL_FORMAT, port))
                .build();
    }

    @Test
    void testResetPassword() {
        forgotPasswordRequest();
        String token = getResetToken();
        resetPassword(token);
        verifyResetPassword();
    }

    private void forgotPasswordRequest() {
        ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest(userEntity.getEmail());
        webClient.post()
                .uri(FORGOT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(forgotPasswordRequest)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    private String getResetToken() {
        verify(emailClient, atLeastOnce()).sendEmail(emailRequestArgumentCaptor.capture());
        EmailRequest emailRequest = emailRequestArgumentCaptor.getValue();
        assertThat(emailRequest).isNotNull();
        assertThat(emailRequest.getTemplateCode()).isEqualTo(Templates.RESET_PASSWORD);
        assertThat(emailRequest.getVariables()).containsKey(TemplateVariablesDictionary.RESET_PASSWORD_URL_KEY);
        String resetPasswordUrl = emailRequest.getVariables().get(TemplateVariablesDictionary.RESET_PASSWORD_URL_KEY);
        MultiValueMap<String, String> parameters =
                UriComponentsBuilder.fromUriString(resetPasswordUrl).build().getQueryParams();
        String token = parameters.getFirst(TOKEN_PARAM);
        assertThat(token).isNotNull();
        return token;
    }

    private void resetPassword(String token) {
        ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest(token, NEW_PASSWORD);
        webClient.post()
                .uri(RESET_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(resetPasswordRequest)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    private void verifyResetPassword() {
        UserEntity actualUser = userService.getById(userEntity.getId());
        assertThat(actualUser).isNotNull();
        //Verify that password has been changed
        assertThat(passwordEncoder.matches(NEW_PASSWORD, actualUser.getPassword())).isTrue();
        //Verify that all tokens has been revoked
        Collection<OAuth2AccessToken> tokens = tokenStore.findTokensByUserName(actualUser.getLogin());
        assertThat(tokens).isEmpty();
    }
}
