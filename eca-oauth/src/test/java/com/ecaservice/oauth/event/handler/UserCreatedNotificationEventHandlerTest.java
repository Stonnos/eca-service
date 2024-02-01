package com.ecaservice.oauth.event.handler;

import com.ecaservice.notification.dto.EmailRequest;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.event.model.UserCreatedNotificationEvent;
import com.ecaservice.oauth.service.mail.dictionary.TemplateVariablesDictionary;
import com.ecaservice.oauth.service.mail.dictionary.Templates;
import org.junit.jupiter.api.Test;

import static com.ecaservice.notification.util.Priority.MEDIUM;
import static com.ecaservice.oauth.TestHelperUtils.createUserEntity;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link UserCreatedNotificationEventHandler} class.
 *
 * @author Roman Batygin
 */
class UserCreatedNotificationEventHandlerTest {

    private static final String PASSWORD = "pa66word!";

    private UserCreatedNotificationEventHandler
            userCreatedNotificationEventHandler = new UserCreatedNotificationEventHandler();

    @Test
    void testEvent() {
        UserEntity userEntity = createUserEntity();
        UserCreatedNotificationEvent event = new UserCreatedNotificationEvent(this, userEntity, PASSWORD);
        EmailRequest actual = userCreatedNotificationEventHandler.handle(event);
        assertThat(actual).isNotNull();
        assertThat(actual.getTemplateCode()).isEqualTo(Templates.NEW_USER);
        assertThat(actual.getReceiver()).isEqualTo(userEntity.getEmail());
        assertThat(actual.getVariables()).isNotEmpty();
        assertThat(actual.getVariables()).containsEntry(TemplateVariablesDictionary.USERNAME_KEY,
                userEntity.getLogin());
        assertThat(actual.getVariables()).containsEntry(TemplateVariablesDictionary.PASSWORD_KEY, PASSWORD);
        assertThat(actual.getPriority()).isEqualTo(MEDIUM);
    }
}
