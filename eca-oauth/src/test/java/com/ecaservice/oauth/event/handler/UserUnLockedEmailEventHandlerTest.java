package com.ecaservice.oauth.event.handler;

import com.ecaservice.oauth.event.model.UserUnLockedEmailEvent;
import com.ecaservice.oauth.service.mail.dictionary.Templates;
import org.junit.jupiter.api.Test;

import static com.ecaservice.oauth.TestHelperUtils.createUserEntity;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link UserUnLockedEmailEventHandler} class.
 *
 * @author Roman Batygin
 */
class UserUnLockedEmailEventHandlerTest {

    private UserUnLockedEmailEventHandler handler = new UserUnLockedEmailEventHandler();

    @Test
    void testHandleEvent() {
        var userEntity = createUserEntity();
        var emailRequest = handler.handle(new UserUnLockedEmailEvent(this, userEntity));
        assertThat(emailRequest).isNotNull();
        assertThat(emailRequest.getTemplateCode()).isEqualTo(Templates.USER_UNLOCKED);
    }
}
