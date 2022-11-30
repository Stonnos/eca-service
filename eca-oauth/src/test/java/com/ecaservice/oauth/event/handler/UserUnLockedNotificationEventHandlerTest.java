package com.ecaservice.oauth.event.handler;

import com.ecaservice.oauth.event.model.UserUnLockedNotificationEvent;
import com.ecaservice.oauth.service.mail.dictionary.Templates;
import org.junit.jupiter.api.Test;

import static com.ecaservice.oauth.TestHelperUtils.createUserEntity;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link UserUnLockedNotificationEventHandler} class.
 *
 * @author Roman Batygin
 */
class UserUnLockedNotificationEventHandlerTest {

    private UserUnLockedNotificationEventHandler handler = new UserUnLockedNotificationEventHandler();

    @Test
    void testHandleEvent() {
        var userEntity = createUserEntity();
        var emailRequest = handler.handle(new UserUnLockedNotificationEvent(this, userEntity));
        assertThat(emailRequest).isNotNull();
        assertThat(emailRequest.getTemplateCode()).isEqualTo(Templates.USER_UNLOCKED);
    }
}
