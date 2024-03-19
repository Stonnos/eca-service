package com.ecaservice.oauth.event.handler;

import com.ecaservice.oauth.event.model.PasswordChangedEmailEvent;
import com.ecaservice.oauth.service.mail.dictionary.Templates;
import org.junit.jupiter.api.Test;

import static com.ecaservice.oauth.TestHelperUtils.createUserEntity;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link PasswordChangedEmailEventHandler} class.
 *
 * @author Roman Batygin
 */
class PasswordChangedEmailEventHandlerTest {

    private PasswordChangedEmailEventHandler handler = new PasswordChangedEmailEventHandler();

    @Test
    void testHandleEvent() {
        var userEntity = createUserEntity();
        var emailRequest = handler.handle(new PasswordChangedEmailEvent(this, userEntity));
        assertThat(emailRequest).isNotNull();
        assertThat(emailRequest.getTemplateCode()).isEqualTo(Templates.PASSWORD_CHANGED);
    }
}
