package com.ecaservice.oauth.event.handler;

import com.ecaservice.oauth.entity.ChangeEmailRequestEntity;
import com.ecaservice.oauth.event.model.EmailChangedNewEmailConfirmedEvent;
import com.ecaservice.oauth.service.mail.dictionary.Templates;
import org.junit.jupiter.api.Test;

import static com.ecaservice.oauth.TestHelperUtils.createUserEntity;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link EmailChangedNewEmailConfirmedEventHandler} class.
 *
 * @author Roman Batygin
 */
class EmailChangedNewEmailConfirmedEventHandlerTest {

    private final EmailChangedNewEmailConfirmedEventHandler handler = new EmailChangedNewEmailConfirmedEventHandler();

    @Test
    void testHandleEvent() {
        var userEntity = createUserEntity();
        var changeEmailRequestEntity = new ChangeEmailRequestEntity();
        var emailRequest =
                handler.handle(new EmailChangedNewEmailConfirmedEvent(this, userEntity, changeEmailRequestEntity));
        assertThat(emailRequest).isNotNull();
        assertThat(emailRequest.getTemplateCode()).isEqualTo(Templates.EMAIL_CHANGED_NEW_EMAIL_CONFIRMED);
        assertThat(emailRequest.getReceiver()).isEqualTo(userEntity.getEmail());
    }
}
