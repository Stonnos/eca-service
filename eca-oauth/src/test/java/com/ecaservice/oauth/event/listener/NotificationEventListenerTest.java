package com.ecaservice.oauth.event.listener;

import com.ecaservice.notification.dto.EmailRequest;
import com.ecaservice.notification.dto.EmailResponse;
import com.ecaservice.oauth.config.ChangePasswordConfig;
import com.ecaservice.oauth.config.ResetPasswordConfig;
import com.ecaservice.oauth.entity.ChangePasswordRequestEntity;
import com.ecaservice.oauth.entity.ResetPasswordRequestEntity;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.event.listener.handler.AbstractNotificationEventHandler;
import com.ecaservice.oauth.event.model.AbstractNotificationEvent;
import com.ecaservice.oauth.event.model.ChangePasswordNotificationEvent;
import com.ecaservice.oauth.event.model.ResetPasswordNotificationEvent;
import com.ecaservice.oauth.event.model.TfaCodeNotificationEvent;
import com.ecaservice.oauth.event.model.UserCreatedEvent;
import com.ecaservice.oauth.model.TokenModel;
import com.ecaservice.oauth.service.UserService;
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
@Import({ResetPasswordConfig.class, NotificationEventListener.class, ChangePasswordConfig.class})
class NotificationEventListenerTest {

    private static final String TFA_CODE = "code";
    private static final String PASSWORD = "pa66word!";
    private static final String TOKEN = "token";
    private static final long USER_ID = 1L;

    @MockBean
    private EmailClient emailClient;
    @MockBean
    private UserService userService;

    @Inject
    private NotificationEventListener notificationEventListener;

    @Captor
    private ArgumentCaptor<EmailRequest> emailRequestArgumentCaptor;

    @Test
    void testTfaCode() {
        UserEntity userEntity = createUserEntity();
        TfaCodeNotificationEvent event = new TfaCodeNotificationEvent(this, userEntity, TFA_CODE);
        internalTestEvent(event, Templates.TFA_CODE);
    }

    @Test
    void testUserCreated() {
        UserEntity userEntity = createUserEntity();
        UserCreatedEvent event = new UserCreatedEvent(this, userEntity, PASSWORD);
        internalTestEvent(event, Templates.NEW_USER);
    }

    @Test
    void testResetPassword() {
        ResetPasswordRequestEntity resetPasswordRequestEntity = createResetPasswordRequestEntity();
        resetPasswordRequestEntity.getUserEntity().setId(USER_ID);
        TokenModel tokenModel = new TokenModel(TOKEN, USER_ID, resetPasswordRequestEntity.getId());
        when(userService.getById(USER_ID)).thenReturn(resetPasswordRequestEntity.getUserEntity());
        ResetPasswordNotificationEvent event = new ResetPasswordNotificationEvent(this, tokenModel);
        internalTestEvent(event, Templates.RESET_PASSWORD);
    }

    @Test
    void testChangePassword() {
        ChangePasswordRequestEntity changePasswordRequestEntity = createChangePasswordRequestEntity(TOKEN);
        changePasswordRequestEntity.getUserEntity().setId(USER_ID);
        TokenModel tokenModel = new TokenModel(TOKEN, USER_ID, changePasswordRequestEntity.getId());
        ChangePasswordNotificationEvent event = new ChangePasswordNotificationEvent(this, tokenModel);
        when(userService.getById(USER_ID)).thenReturn(changePasswordRequestEntity.getUserEntity());
        internalTestEvent(event, Templates.CHANGE_PASSWORD);
    }

    @Test
    void testThrowIllegalStateException() {
        AbstractNotificationEvent event = new AbstractNotificationEvent(this) {
        };
        assertThrows(IllegalStateException.class, () -> notificationEventListener.handleNotificationEvent(event));
    }

    private void internalTestEvent(AbstractNotificationEvent event, String expectedTemplateCode) {
        when(emailClient.sendEmail(any(EmailRequest.class))).thenReturn(new EmailResponse());
        notificationEventListener.handleNotificationEvent(event);
        verify(emailClient, atLeastOnce()).sendEmail(emailRequestArgumentCaptor.capture());
        EmailRequest actual = emailRequestArgumentCaptor.getValue();
        assertThat(actual).isNotNull();
        assertThat(actual.getTemplateCode()).isEqualTo(expectedTemplateCode);
    }
}
