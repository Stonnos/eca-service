package com.ecaservice.oauth.event.listener.handler;

import com.ecaservice.notification.dto.EmailRequest;
import com.ecaservice.oauth.config.ResetPasswordConfig;
import com.ecaservice.oauth.entity.ResetPasswordRequestEntity;
import com.ecaservice.oauth.event.model.ResetPasswordNotificationEvent;
import com.ecaservice.oauth.model.TokenModel;
import com.ecaservice.oauth.service.UserService;
import com.ecaservice.oauth.service.mail.dictionary.TemplateVariablesDictionary;
import com.ecaservice.oauth.service.mail.dictionary.Templates;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static com.ecaservice.notification.util.Priority.MEDIUM;
import static com.ecaservice.oauth.TestHelperUtils.createResetPasswordRequestEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit tests for class {@link ResetPasswordNotificationEventHandler}.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties
@TestPropertySource("classpath:application.properties")
@Import({ResetPasswordConfig.class, ResetPasswordNotificationEventHandler.class})
class ResetPasswordNotificationEventHandlerTest {

    private static final String TOKEN = "token";
    private static final long USER_ID = 1L;

    private static final String RESET_PASSWORD_URL_FORMAT = "%s/reset-password/?token=%s";

    @Inject
    private ResetPasswordConfig resetPasswordConfig;
    @Inject
    private ResetPasswordNotificationEventHandler eventHandler;

    @MockBean
    private UserService userService;

    @Test
    void testEvent() {
        ResetPasswordRequestEntity resetPasswordRequestEntity = createResetPasswordRequestEntity();
        resetPasswordRequestEntity.getUserEntity().setId(USER_ID);
        TokenModel tokenModel = new TokenModel(TOKEN, USER_ID, resetPasswordRequestEntity.getId());
        ResetPasswordNotificationEvent resetPasswordNotificationEvent =
                new ResetPasswordNotificationEvent(this, tokenModel);
        when(userService.getById(USER_ID)).thenReturn(resetPasswordRequestEntity.getUserEntity());
        EmailRequest actual = eventHandler.handle(resetPasswordNotificationEvent);
        assertThat(actual).isNotNull();
        assertThat(actual.getTemplateCode()).isEqualTo(Templates.RESET_PASSWORD);
        assertThat(actual.getReceiver()).isEqualTo(resetPasswordRequestEntity.getUserEntity().getEmail());
        assertThat(actual.getVariables()).isNotEmpty();
        assertThat(actual.getVariables()).containsEntry(TemplateVariablesDictionary.VALIDITY_MINUTES_KEY,
                String.valueOf(resetPasswordConfig.getValidityMinutes()));
        assertThat(actual.getVariables()).containsEntry(TemplateVariablesDictionary.RESET_PASSWORD_URL_KEY,
                String.format(RESET_PASSWORD_URL_FORMAT, resetPasswordConfig.getBaseUrl(),
                        resetPasswordRequestEntity.getToken()));
        assertThat(actual.getPriority()).isEqualTo(MEDIUM);
    }
}
