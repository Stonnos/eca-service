package com.ecaservice.oauth.event.handler;

import com.ecaservice.oauth.event.model.PasswordResetEmailEvent;
import com.ecaservice.oauth.service.mail.dictionary.Templates;
import org.junit.jupiter.api.Test;

import static com.ecaservice.oauth.TestHelperUtils.createUserEntity;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link PasswordResetEmailEventHandler} class.
 *
 * @author Roman Batygin
 */
class PasswordResetEmailEventHandlerTest {

    private PasswordResetEmailEventHandler handler = new PasswordResetEmailEventHandler();

    @Test
    void testHandleEvent() {
        var userEntity = createUserEntity();
        var emailRequest = handler.handle(new PasswordResetEmailEvent(this, userEntity));
        assertThat(emailRequest).isNotNull();
        assertThat(emailRequest.getTemplateCode()).isEqualTo(Templates.PASSWORD_RESET);
    }
}
