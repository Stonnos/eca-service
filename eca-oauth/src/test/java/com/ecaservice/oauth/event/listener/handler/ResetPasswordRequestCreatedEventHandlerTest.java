package com.ecaservice.oauth.event.listener.handler;

import com.ecaservice.notification.dto.EmailRequest;
import com.ecaservice.oauth.config.ResetPasswordConfig;
import com.ecaservice.oauth.entity.ResetPasswordRequestEntity;
import com.ecaservice.oauth.event.model.ResetPasswordRequestCreatedEvent;
import com.ecaservice.oauth.service.mail.dictionary.TemplateVariablesDictionary;
import com.ecaservice.oauth.service.mail.dictionary.Templates;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static com.ecaservice.oauth.TestHelperUtils.createResetPasswordRequestEntity;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for class {@link ResetPasswordRequestCreatedEventHandler}.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties
@TestPropertySource("classpath:application.properties")
@Import({ResetPasswordConfig.class, ResetPasswordRequestCreatedEventHandler.class})
class ResetPasswordRequestCreatedEventHandlerTest {

    private static final String RESET_PASSWORD_URL_FORMAT = "%s/reset-password/?token=%s";

    @Inject
    private ResetPasswordConfig resetPasswordConfig;
    @Inject
    private ResetPasswordRequestCreatedEventHandler eventHandler;

    @Test
    void testEvent() {
        ResetPasswordRequestEntity resetPasswordRequestEntity = createResetPasswordRequestEntity();
        ResetPasswordRequestCreatedEvent resetPasswordRequestCreatedEvent =
                new ResetPasswordRequestCreatedEvent(this, resetPasswordRequestEntity);
        EmailRequest actual = eventHandler.handle(resetPasswordRequestCreatedEvent);
        assertThat(actual).isNotNull();
        assertThat(actual.getTemplateCode()).isEqualTo(Templates.RESET_PASSWORD);
        assertThat(actual.getVariables()).isNotEmpty();
        assertThat(actual.getVariables()).containsEntry(TemplateVariablesDictionary.VALIDITY_MINUTES_KEY,
                String.valueOf(resetPasswordConfig.getValidityMinutes()));
        assertThat(actual.getVariables()).containsEntry(TemplateVariablesDictionary.RESET_PASSWORD_URL_KEY,
                String.format(RESET_PASSWORD_URL_FORMAT, resetPasswordConfig.getBaseUrl(),
                        resetPasswordRequestEntity.getToken()));
    }
}
