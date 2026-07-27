package com.ecaservice.oauth.event.handler;

import com.ecaservice.notification.dto.EmailRequest;
import com.ecaservice.oauth.config.AppProperties;
import com.ecaservice.oauth.event.model.ChangeEmailRequestConfirmNewEmailEvent;
import com.ecaservice.oauth.model.TokenModel;
import com.ecaservice.oauth.service.mail.dictionary.TemplateVariablesDictionary;
import com.ecaservice.oauth.service.mail.dictionary.Templates;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static com.ecaservice.notification.util.Priority.MEDIUM;
import static com.ecaservice.oauth.TestHelperUtils.createChangeEmailRequestEntity;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for class {@link ChangeEmailRequestConfirmNewEmailEventHandler}.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties
@TestPropertySource("classpath:application.properties")
@Import({AppProperties.class, ChangeEmailRequestConfirmNewEmailEventHandler.class})
class ChangeEmailRequestConfirmNewEmailEventHandlerTest {

    private static final String CONFIRMATION_CODE = "token";
    private static final long USER_ID = 1L;

    private static final String NEW_EMAIL = "newemail@mail.ru";

    @Autowired
    private AppProperties appProperties;
    @Autowired
    private ChangeEmailRequestConfirmNewEmailEventHandler eventHandler;

    @Test
    void testEvent() {
        var changeEmailRequestEntity = createChangeEmailRequestEntity(CONFIRMATION_CODE);
        changeEmailRequestEntity.getUserEntity().setId(USER_ID);
        var tokenModel = TokenModel.builder()
                .token(UUID.randomUUID().toString())
                .confirmationCode(CONFIRMATION_CODE)
                .tokenId(changeEmailRequestEntity.getId())
                .login(changeEmailRequestEntity.getUserEntity().getLogin())
                .email(changeEmailRequestEntity.getUserEntity().getEmail())
                .build();
        var changeEmailNotificationEvent = new ChangeEmailRequestConfirmNewEmailEvent(this, tokenModel, NEW_EMAIL);
        EmailRequest actual = eventHandler.handle(changeEmailNotificationEvent);
        assertThat(actual).isNotNull();
        assertThat(actual.getTemplateCode()).isEqualTo(Templates.CHANGE_EMAIL_CONFIRM_NEW_EMAIL);
        assertThat(actual.getReceiver()).isEqualTo(NEW_EMAIL);
        assertThat(actual.getVariables()).isNotEmpty();
        assertThat(actual.getVariables()).containsEntry(TemplateVariablesDictionary.VALIDITY_MINUTES_KEY,
                String.valueOf(appProperties.getChangeEmail().getValidityMinutes()));
        assertThat(actual.getVariables()).containsEntry(TemplateVariablesDictionary.CONFIRMATION_CODE_KEY,
                tokenModel.getConfirmationCode());
        assertThat(actual.getPriority()).isEqualTo(MEDIUM);
    }
}
