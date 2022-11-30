package com.ecaservice.oauth.event.handler;

import com.ecaservice.oauth.event.model.PasswordChangedNotificationEvent;
import com.ecaservice.oauth.service.mail.dictionary.Templates;
import org.junit.jupiter.api.Test;

import static com.ecaservice.oauth.TestHelperUtils.createUserEntity;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link PasswordChangedNotificationEventHandler} class.
 *
 * @author Roman Batygin
 */
class PasswordChangedNotificationEventHandlerTest {

    private PasswordChangedNotificationEventHandler handler = new PasswordChangedNotificationEventHandler();

    @Test
    void testHandleEvent() {
        var userEntity = createUserEntity();
        var emailRequest = handler.handle(new PasswordChangedNotificationEvent(this, userEntity));
        assertThat(emailRequest).isNotNull();
        assertThat(emailRequest.getTemplateCode()).isEqualTo(Templates.PASSWORD_CHANGED);
    }
}
