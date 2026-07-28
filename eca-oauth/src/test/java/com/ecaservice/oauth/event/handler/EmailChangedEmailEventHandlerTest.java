package com.ecaservice.oauth.event.handler;

import com.ecaservice.oauth.entity.ChangeEmailRequestEntity;
import com.ecaservice.oauth.event.model.EmailChangedEmailEvent;
import com.ecaservice.oauth.service.mail.dictionary.TemplateVariablesDictionary;
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

    private static final String OLD_MAIL = "old@mail.com";
    private static final String NEW_EMAIL = "new@mail.com";

    private final EmailChangedEmailEventHandler handler = new EmailChangedEmailEventHandler();

    @Test
    void testHandleEvent() {
        var userEntity = createUserEntity();
        var changeEmailRequestEntity = new ChangeEmailRequestEntity();
        changeEmailRequestEntity.setNewEmail(NEW_EMAIL);
        changeEmailRequestEntity.setOldEmail(OLD_MAIL);
        var emailRequest = handler.handle(new EmailChangedEmailEvent(this, userEntity, changeEmailRequestEntity));
        assertThat(emailRequest).isNotNull();
        assertThat(emailRequest.getTemplateCode()).isEqualTo(Templates.EMAIL_CHANGED);
        assertThat(emailRequest.getReceiver()).isEqualTo(changeEmailRequestEntity.getOldEmail());
        assertThat(emailRequest.getVariables()).containsEntry(TemplateVariablesDictionary.NEW_EMAIL, NEW_EMAIL);
    }
}
