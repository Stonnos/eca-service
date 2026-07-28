package com.ecaservice.oauth.event.handler;

import com.ecaservice.notification.dto.EmailRequest;
import com.ecaservice.oauth.event.model.ForceSetPasswordRequestConfirmationCodeEmailEvent;
import com.ecaservice.oauth.model.TokenModel;
import com.ecaservice.oauth.service.mail.dictionary.TemplateVariablesDictionary;
import com.ecaservice.oauth.service.mail.dictionary.Templates;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static com.ecaservice.notification.util.Priority.MEDIUM;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for class {@link ForceSetPasswordConfirmationCodeEmailEventHandler}.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties
@TestPropertySource("classpath:application.properties")
@Import(ForceSetPasswordConfirmationCodeEmailEventHandler.class)
class ForceSetPasswordConfirmationCodeEmailEventHandlerTest {

    private static final String CONFIRMATION_CODE = "123204";
    private static final String EMAIL = "mail@mail.com";

    @Autowired
    private ForceSetPasswordConfirmationCodeEmailEventHandler eventHandler;

    @Test
    void testEvent() {
        var tokenModel = TokenModel.builder()
                .confirmationCode(CONFIRMATION_CODE)
                .email(EMAIL)
                .token(UUID.randomUUID().toString())
                .build();
        var event = new ForceSetPasswordRequestConfirmationCodeEmailEvent(this, tokenModel);
        EmailRequest actual = eventHandler.handle(event);
        assertThat(actual).isNotNull();
        assertThat(actual.getTemplateCode()).isEqualTo(Templates.SET_PASSWORD_CONFIRMATION_CODE);
        assertThat(actual.getReceiver()).isEqualTo(tokenModel.getEmail());
        assertThat(actual.getVariables()).isNotEmpty();
        assertThat(actual.getVariables()).containsEntry(TemplateVariablesDictionary.CONFIRMATION_CODE_KEY,
                tokenModel.getConfirmationCode());
        assertThat(actual.getPriority()).isEqualTo(MEDIUM);
    }
}
