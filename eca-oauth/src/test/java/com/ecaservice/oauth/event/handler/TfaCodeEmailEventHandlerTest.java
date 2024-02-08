package com.ecaservice.oauth.event.handler;

import com.ecaservice.notification.dto.EmailRequest;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.event.model.TfaCodeEmailEvent;
import com.ecaservice.oauth.service.mail.dictionary.TemplateVariablesDictionary;
import com.ecaservice.oauth.service.mail.dictionary.Templates;
import org.junit.jupiter.api.Test;

import static com.ecaservice.notification.util.Priority.HIGHEST;
import static com.ecaservice.oauth.TestHelperUtils.createUserEntity;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link TfaCodeEmailEventHandler} class.
 *
 * @author Roman Batygin
 */
class TfaCodeEmailEventHandlerTest {

    private static final String TFA_CODE = "code";

    private TfaCodeEmailEventHandler tfaCodeNotificationEventHandler = new TfaCodeEmailEventHandler();

    @Test
    void testEvent() {
        UserEntity userEntity = createUserEntity();
        TfaCodeEmailEvent event = new TfaCodeEmailEvent(this, userEntity, TFA_CODE);
        EmailRequest emailRequest = tfaCodeNotificationEventHandler.handle(event);
        assertThat(emailRequest).isNotNull();
        assertThat(emailRequest.getTemplateCode()).isEqualTo(Templates.TFA_CODE);
        assertThat(emailRequest.getReceiver()).isEqualTo(userEntity.getEmail());
        assertThat(emailRequest.getVariables()).isNotEmpty();
        assertThat(emailRequest.getVariables()).containsEntry(TemplateVariablesDictionary.TFA_CODE, TFA_CODE);
        assertThat(emailRequest.getPriority()).isEqualTo(HIGHEST);
    }
}
