package com.ecaservice.oauth.event.listener;

import com.ecaservice.notification.dto.EmailRequest;
import com.ecaservice.notification.dto.EmailResponse;
import com.ecaservice.oauth.config.AppProperties;
import com.ecaservice.oauth.event.listener.handler.AbstractNotificationEventHandler;
import com.ecaservice.oauth.event.model.AbstractNotificationEvent;
import com.ecaservice.oauth.event.model.ChangeEmailRequestNotificationEvent;
import com.ecaservice.oauth.event.model.ChangePasswordRequestNotificationEvent;
import com.ecaservice.oauth.event.model.EmailChangedNotificationEvent;
import com.ecaservice.oauth.event.model.PasswordChangedNotificationEvent;
import com.ecaservice.oauth.event.model.PasswordResetNotificationEvent;
import com.ecaservice.oauth.event.model.ResetPasswordRequestNotificationEvent;
import com.ecaservice.oauth.event.model.TfaCodeNotificationEvent;
import com.ecaservice.oauth.event.model.UserCreatedEvent;
import com.ecaservice.oauth.model.TokenModel;
import com.ecaservice.oauth.service.mail.EmailClient;
import com.ecaservice.oauth.service.mail.dictionary.Templates;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static com.ecaservice.oauth.TestHelperUtils.createChangeEmailRequestEntity;
import static com.ecaservice.oauth.TestHelperUtils.createChangePasswordRequestEntity;
import static com.ecaservice.oauth.TestHelperUtils.createResetPasswordRequestEntity;
import static com.ecaservice.oauth.TestHelperUtils.createUserEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link NotificationEventListener} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties
@TestPropertySource("classpath:application.properties")
@ComponentScan(basePackageClasses = AbstractNotificationEventHandler.class)
@Import({NotificationEventListener.class, AppProperties.class})
class NotificationEventListenerTest {

    private static final String TFA_CODE = "code";
    private static final String PASSWORD = "pa66word!";
    private static final String TOKEN = "token";
    private static final long USER_ID = 1L;
    private static final String NEW_EMAIL = "newemail@mail.ru";

    @MockBean
    private EmailClient emailClient;

    @Inject
    private NotificationEventListener notificationEventListener;

    @Captor
    private ArgumentCaptor<EmailRequest> emailRequestArgumentCaptor;

    @Test
    void testTfaCode() {
        var userEntity = createUserEntity();
        var event = new TfaCodeNotificationEvent(this, userEntity, TFA_CODE);
        internalTestEvent(event, Templates.TFA_CODE, userEntity.getEmail());
    }

    @Test
    void testUserCreated() {
        var userEntity = createUserEntity();
        var event = new UserCreatedEvent(this, userEntity, PASSWORD);
        internalTestEvent(event, Templates.NEW_USER, userEntity.getEmail());
    }

    @Test
    void testResetPassword() {
        var resetPasswordRequestEntity = createResetPasswordRequestEntity();
        resetPasswordRequestEntity.getUserEntity().setId(USER_ID);
        var tokenModel = TokenModel.builder()
                .token(TOKEN)
                .tokenId(resetPasswordRequestEntity.getId())
                .login(resetPasswordRequestEntity.getUserEntity().getLogin())
                .email(resetPasswordRequestEntity.getUserEntity().getEmail())
                .build();
        var event = new ResetPasswordRequestNotificationEvent(this, tokenModel);
        internalTestEvent(event, Templates.RESET_PASSWORD, resetPasswordRequestEntity.getUserEntity().getEmail());
    }

    @Test
    void testChangePassword() {
        var changePasswordRequestEntity = createChangePasswordRequestEntity(TOKEN);
        changePasswordRequestEntity.getUserEntity().setId(USER_ID);
        var tokenModel = TokenModel.builder()
                .token(TOKEN)
                .tokenId(changePasswordRequestEntity.getId())
                .login(changePasswordRequestEntity.getUserEntity().getLogin())
                .email(changePasswordRequestEntity.getUserEntity().getEmail())
                .build();
        var event = new ChangePasswordRequestNotificationEvent(this, tokenModel);
        internalTestEvent(event, Templates.CHANGE_PASSWORD, changePasswordRequestEntity.getUserEntity().getEmail());
    }

    @Test
    void testChangeEmail() {
        var changeEmailRequestEntity = createChangeEmailRequestEntity(TOKEN);
        changeEmailRequestEntity.getUserEntity().setId(USER_ID);
        var tokenModel = TokenModel.builder()
                .token(TOKEN)
                .tokenId(changeEmailRequestEntity.getId())
                .login(changeEmailRequestEntity.getUserEntity().getLogin())
                .email(changeEmailRequestEntity.getUserEntity().getEmail())
                .build();
        var event = new ChangeEmailRequestNotificationEvent(this, tokenModel, NEW_EMAIL);
        internalTestEvent(event, Templates.CHANGE_EMAIL, NEW_EMAIL);
    }

    @Test
    void testThrowIllegalStateException() {
        var event = new AbstractNotificationEvent(this) {
            @Override
            public String getReceiver() {
                return NEW_EMAIL;
            }
        };
        assertThrows(IllegalStateException.class, () -> notificationEventListener.handleNotificationEvent(event));
    }

    @Test
    void testEmailChanged() {
        var userEntity = createUserEntity();
        var event = new EmailChangedNotificationEvent(this, userEntity);
        internalTestEvent(event, Templates.EMAIL_CHANGED, userEntity.getEmail());
    }

    @Test
    void testPasswordChanged() {
        var userEntity = createUserEntity();
        var event = new PasswordChangedNotificationEvent(this, userEntity);
        internalTestEvent(event, Templates.PASSWORD_CHANGED, userEntity.getEmail());
    }

    @Test
    void testPasswordReset() {
        var userEntity = createUserEntity();
        var event = new PasswordResetNotificationEvent(this, userEntity);
        internalTestEvent(event, Templates.PASSWORD_RESET, userEntity.getEmail());
    }

    private void internalTestEvent(AbstractNotificationEvent event,
                                   String expectedTemplateCode,
                                   String expectedReceiver) {
        when(emailClient.sendEmail(any(EmailRequest.class))).thenReturn(new EmailResponse());
        notificationEventListener.handleNotificationEvent(event);
        verify(emailClient, atLeastOnce()).sendEmail(emailRequestArgumentCaptor.capture());
        EmailRequest actual = emailRequestArgumentCaptor.getValue();
        assertThat(actual).isNotNull();
        assertThat(actual.getTemplateCode()).isEqualTo(expectedTemplateCode);
        assertThat(actual.getReceiver()).isEqualTo(expectedReceiver);
    }
}
