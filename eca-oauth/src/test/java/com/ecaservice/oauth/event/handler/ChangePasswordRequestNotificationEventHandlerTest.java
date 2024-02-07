package com.ecaservice.oauth.event.handler;

import com.ecaservice.notification.dto.EmailRequest;
import com.ecaservice.oauth.config.AppProperties;
import com.ecaservice.oauth.event.model.ChangePasswordRequestNotificationEvent;
import com.ecaservice.oauth.model.TokenModel;
import com.ecaservice.oauth.service.mail.dictionary.TemplateVariablesDictionary;
import com.ecaservice.oauth.service.mail.dictionary.Templates;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static com.ecaservice.notification.util.Priority.MEDIUM;
import static com.ecaservice.oauth.TestHelperUtils.createChangePasswordRequestEntity;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for class {@link ChangePasswordRequestNotificationEventHandler}.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties
@TestPropertySource("classpath:application.properties")
@Import({AppProperties.class, ChangePasswordRequestNotificationEventHandler.class})
class ChangePasswordRequestNotificationEventHandlerTest {

    private static final String CONFIRMATION_CODE = "token";
    private static final long USER_ID = 1L;

    @Inject
    private AppProperties appProperties;
    @Inject
    private ChangePasswordRequestNotificationEventHandler eventHandler;

    @Test
    void testEvent() {
        var changePasswordRequestEntity = createChangePasswordRequestEntity(CONFIRMATION_CODE);
        changePasswordRequestEntity.getUserEntity().setId(USER_ID);
        var tokenModel = TokenModel.builder()
                .confirmationCode(CONFIRMATION_CODE)
                .token(changePasswordRequestEntity.getToken())
                .tokenId(changePasswordRequestEntity.getId())
                .login(changePasswordRequestEntity.getUserEntity().getLogin())
                .email(changePasswordRequestEntity.getUserEntity().getEmail())
                .build();
        var changePasswordNotificationEvent = new ChangePasswordRequestNotificationEvent(this, tokenModel);
        EmailRequest actual = eventHandler.handle(changePasswordNotificationEvent);
        assertThat(actual).isNotNull();
        assertThat(actual.getTemplateCode()).isEqualTo(Templates.CHANGE_PASSWORD);
        assertThat(actual.getReceiver()).isEqualTo(changePasswordRequestEntity.getUserEntity().getEmail());
        assertThat(actual.getVariables()).isNotEmpty();
        assertThat(actual.getVariables()).containsEntry(TemplateVariablesDictionary.VALIDITY_MINUTES_KEY,
                String.valueOf(appProperties.getChangePassword().getValidityMinutes()));
        assertThat(actual.getVariables()).containsEntry(TemplateVariablesDictionary.CONFIRMATION_CODE_KEY,
                tokenModel.getConfirmationCode());
        assertThat(actual.getPriority()).isEqualTo(MEDIUM);
    }
}
