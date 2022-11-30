package com.ecaservice.oauth.event.handler;

import com.ecaservice.oauth.event.model.EmailChangedNotificationEvent;
import com.ecaservice.oauth.service.mail.dictionary.Templates;
import org.junit.jupiter.api.Test;

import static com.ecaservice.oauth.TestHelperUtils.createUserEntity;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link EmailChangedNotificationEventHandler} class.
 *
 * @author Roman Batygin
 */
class EmailChangedNotificationEventHandlerTest {

    private EmailChangedNotificationEventHandler handler = new EmailChangedNotificationEventHandler();

    @Test
    void testHandleEvent() {
        var userEntity = createUserEntity();
        var emailRequest = handler.handle(new EmailChangedNotificationEvent(this, userEntity));
        assertThat(emailRequest).isNotNull();
        assertThat(emailRequest.getTemplateCode()).isEqualTo(Templates.EMAIL_CHANGED);
    }
}
