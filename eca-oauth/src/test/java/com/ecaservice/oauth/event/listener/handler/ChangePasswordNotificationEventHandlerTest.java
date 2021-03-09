package com.ecaservice.oauth.event.listener.handler;

import com.ecaservice.notification.dto.EmailRequest;
import com.ecaservice.oauth.config.ChangePasswordConfig;
import com.ecaservice.oauth.entity.ChangePasswordRequestEntity;
import com.ecaservice.oauth.event.model.ChangePasswordNotificationEvent;
import com.ecaservice.oauth.service.mail.dictionary.TemplateVariablesDictionary;
import com.ecaservice.oauth.service.mail.dictionary.Templates;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static com.ecaservice.oauth.TestHelperUtils.createChangePasswordRequestEntity;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for class {@link ChangePasswordNotificationEventHandler}.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties
@TestPropertySource("classpath:application.properties")
@Import({ChangePasswordConfig.class, ChangePasswordNotificationEventHandler.class})
class ChangePasswordNotificationEventHandlerTest {

    private static final String CHANGE_PASSWORD_URL_FORMAT = "%s/change-password/?token=%s";

    @Inject
    private ChangePasswordConfig changePasswordConfig;
    @Inject
    private ChangePasswordNotificationEventHandler eventHandler;

    @Test
    void testEvent() {
        ChangePasswordRequestEntity changePasswordRequestEntity = createChangePasswordRequestEntity();
        ChangePasswordNotificationEvent changePasswordNotificationEvent =
                new ChangePasswordNotificationEvent(this, changePasswordRequestEntity);
        EmailRequest actual = eventHandler.handle(changePasswordNotificationEvent);
        assertThat(actual).isNotNull();
        assertThat(actual.getTemplateCode()).isEqualTo(Templates.CHANGE_PASSWORD);
        assertThat(actual.getReceiver()).isEqualTo(changePasswordRequestEntity.getUserEntity().getEmail());
        assertThat(actual.getVariables()).isNotEmpty();
        assertThat(actual.getVariables()).containsEntry(TemplateVariablesDictionary.VALIDITY_MINUTES_KEY,
                String.valueOf(changePasswordConfig.getValidityMinutes()));
        assertThat(actual.getVariables()).containsEntry(TemplateVariablesDictionary.CHANGE_PASSWORD_URL_KEY,
                String.format(CHANGE_PASSWORD_URL_FORMAT, changePasswordConfig.getBaseUrl(),
                        changePasswordRequestEntity.getToken()));
    }
}
