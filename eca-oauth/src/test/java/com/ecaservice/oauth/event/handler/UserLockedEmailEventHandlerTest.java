package com.ecaservice.oauth.event.handler;

import com.ecaservice.oauth.event.model.UserLockedEmailEvent;
import com.ecaservice.oauth.service.mail.dictionary.Templates;
import org.junit.jupiter.api.Test;

import static com.ecaservice.oauth.TestHelperUtils.createUserEntity;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link UserLockedEmailEventHandler} class.
 *
 * @author Roman Batygin
 */
class UserLockedEmailEventHandlerTest {

    private UserLockedEmailEventHandler handler = new UserLockedEmailEventHandler();

    @Test
    void testHandleEvent() {
        var userEntity = createUserEntity();
        var emailRequest = handler.handle(new UserLockedEmailEvent(this, userEntity));
        assertThat(emailRequest).isNotNull();
        assertThat(emailRequest.getTemplateCode()).isEqualTo(Templates.USER_LOCKED);
    }
}
