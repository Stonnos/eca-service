package com.ecaservice.oauth.event.handler;

import com.ecaservice.oauth.entity.ChangeEmailRequestEntity;
import com.ecaservice.oauth.event.model.EmailChangedEmailEvent;
import com.ecaservice.oauth.service.mail.dictionary.Templates;
import org.junit.jupiter.api.Test;

import static com.ecaservice.oauth.TestHelperUtils.createUserEntity;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link EmailChangedEmailEventHandler} class.
 *
 * @author Roman Batygin
 */
class EmailChangedEmailEventHandlerTest {

    private EmailChangedEmailEventHandler handler = new EmailChangedEmailEventHandler();

    @Test
    void testHandleEvent() {
        var userEntity = createUserEntity();
        var emailRequest = handler.handle(new EmailChangedEmailEvent(this, userEntity, new ChangeEmailRequestEntity()));
        assertThat(emailRequest).isNotNull();
        assertThat(emailRequest.getTemplateCode()).isEqualTo(Templates.EMAIL_CHANGED);
    }
}
