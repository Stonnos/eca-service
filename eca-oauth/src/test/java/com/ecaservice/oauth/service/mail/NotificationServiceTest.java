package com.ecaservice.oauth.service.mail;

import com.ecaservice.notification.dto.EmailRequest;
import com.ecaservice.notification.dto.EmailResponse;
import com.ecaservice.notification.dto.EmailType;
import com.ecaservice.oauth.config.ResetPasswordConfig;
import com.ecaservice.oauth.entity.ResetPasswordRequestEntity;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.service.mail.dictionary.TemplateVariablesDictionary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.util.UUID;

import static com.ecaservice.oauth.TestHelperUtils.createResetPasswordRequestEntity;
import static com.ecaservice.oauth.TestHelperUtils.createUserEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link NotificationService} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties
@TestPropertySource("classpath:application.properties")
@Import(ResetPasswordConfig.class)
class NotificationServiceTest {

    private static final String RESET_PASSWORD_URL_FORMAT = "%s/reset-password/?token=%s";
    private static final String PASSWORD = "pa66word!";

    @Mock
    private EmailClient emailClient;
    @Inject
    private ResetPasswordConfig resetPasswordConfig;

    private NotificationService notificationService;

    @Captor
    private ArgumentCaptor<EmailRequest> emailRequestArgumentCaptor;

    @BeforeEach
    void init() {
        notificationService = new NotificationService(resetPasswordConfig, emailClient);
    }

    @Test
    void testNotifyUserCreated() {
        UserEntity userEntity = createUserEntity();
        when(emailClient.sendEmail(any(EmailRequest.class))).thenReturn(new EmailResponse());
        notificationService.notifyUserCreated(userEntity, PASSWORD);
        verify(emailClient, atLeastOnce()).sendEmail(emailRequestArgumentCaptor.capture());
        EmailRequest actual = emailRequestArgumentCaptor.getValue();
        assertThat(actual).isNotNull();
        assertThat(actual.getTemplateType()).isEqualTo(EmailType.NEW_USER);
        assertThat(actual.getEmailMessageVariables()).isNotEmpty();
        assertThat(actual.getEmailMessageVariables()).containsEntry(TemplateVariablesDictionary.USERNAME_KEY,
                userEntity.getLogin());
        assertThat(actual.getEmailMessageVariables()).containsEntry(TemplateVariablesDictionary.PASSWORD_KEY, PASSWORD);
    }

    @Test
    void testSendResetPasswordLink() {
        when(emailClient.sendEmail(any(EmailRequest.class))).thenReturn(new EmailResponse());
        ResetPasswordRequestEntity resetPasswordRequestEntity = createResetPasswordRequestEntity();
        notificationService.sendResetPasswordLink(resetPasswordRequestEntity);
        verify(emailClient, atLeastOnce()).sendEmail(emailRequestArgumentCaptor.capture());
        EmailRequest actual = emailRequestArgumentCaptor.getValue();
        assertThat(actual).isNotNull();
        assertThat(actual.getTemplateType()).isEqualTo(EmailType.RESET_PASSWORD);
        assertThat(actual.getEmailMessageVariables()).isNotEmpty();
        assertThat(actual.getEmailMessageVariables()).containsEntry(TemplateVariablesDictionary.VALIDITY_MINUTES_KEY,
                resetPasswordConfig.getValidityMinutes());
        assertThat(actual.getEmailMessageVariables()).containsEntry(TemplateVariablesDictionary.RESET_PASSWORD_URL_KEY,
                String.format(RESET_PASSWORD_URL_FORMAT, resetPasswordConfig.getBaseUrl(),
                        resetPasswordRequestEntity.getToken()));
    }

    @Test
    void testSendTfaCode() {
        when(emailClient.sendEmail(any(EmailRequest.class))).thenReturn(new EmailResponse());
        UserEntity userEntity = createUserEntity();
        String code = UUID.randomUUID().toString();
        notificationService.sendTfaCode(userEntity.getEmail(), code);
        verify(emailClient, atLeastOnce()).sendEmail(emailRequestArgumentCaptor.capture());
        EmailRequest actual = emailRequestArgumentCaptor.getValue();
        assertThat(actual).isNotNull();
        assertThat(actual.getTemplateType()).isEqualTo(EmailType.TFA_CODE);
        assertThat(actual.getEmailMessageVariables()).isNotEmpty();
        assertThat(actual.getEmailMessageVariables()).containsEntry(TemplateVariablesDictionary.TFA_CODE, code);
    }
}
