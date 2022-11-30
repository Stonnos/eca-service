package com.ecaservice.oauth.event.handler;

import com.ecaservice.notification.dto.EmailRequest;
import com.ecaservice.oauth.config.AppProperties;
import com.ecaservice.oauth.event.model.ChangeEmailRequestNotificationEvent;
import com.ecaservice.oauth.model.TokenModel;
import com.ecaservice.oauth.service.mail.dictionary.TemplateVariablesDictionary;
import com.ecaservice.oauth.service.mail.dictionary.Templates;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static com.ecaservice.notification.util.Priority.MEDIUM;
import static com.ecaservice.oauth.TestHelperUtils.createChangeEmailRequestEntity;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for class {@link ChangeEmailRequestNotificationEventHandler}.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties
@TestPropertySource("classpath:application.properties")
@Import({AppProperties.class, ChangeEmailRequestNotificationEventHandler.class})
class ChangeEmailRequestNotificationEventHandlerTest {

    private static final long MINUTES_IN_HOUR = 60L;
    private static final String TOKEN = "token";
    private static final long USER_ID = 1L;

    private static final String NEW_EMAIL = "newemail@mail.ru";

    @Inject
    private AppProperties appProperties;
    @Inject
    private ChangeEmailRequestNotificationEventHandler eventHandler;

    @Test
    void testEvent() {
        var changeEmailRequestEntity = createChangeEmailRequestEntity(TOKEN);
        changeEmailRequestEntity.getUserEntity().setId(USER_ID);
        var tokenModel = TokenModel.builder()
                .token(TOKEN)
                .tokenId(changeEmailRequestEntity.getId())
                .login(changeEmailRequestEntity.getUserEntity().getLogin())
                .email(changeEmailRequestEntity.getUserEntity().getEmail())
                .build();
        var changeEmailNotificationEvent = new ChangeEmailRequestNotificationEvent(this, tokenModel, NEW_EMAIL);
        EmailRequest actual = eventHandler.handle(changeEmailNotificationEvent);
        assertThat(actual).isNotNull();
        assertThat(actual.getTemplateCode()).isEqualTo(Templates.CHANGE_EMAIL);
        assertThat(actual.getReceiver()).isEqualTo(changeEmailRequestEntity.getUserEntity().getEmail());
        assertThat(actual.getVariables()).isNotEmpty();
        Long validityHours = appProperties.getChangeEmail().getValidityMinutes() / MINUTES_IN_HOUR;
        assertThat(actual.getVariables()).containsEntry(TemplateVariablesDictionary.VALIDITY_HOURS_KEY,
                String.valueOf(validityHours));
        String tokenEndpoint = String.format(appProperties.getChangeEmail().getUrl(), TOKEN);
        String expectedUrl = String.format("%s%s", appProperties.getWebExternalBaseUrl(), tokenEndpoint);
        assertThat(actual.getVariables()).containsEntry(TemplateVariablesDictionary.CHANGE_EMAIL_URL_KEY,
                expectedUrl);
        assertThat(actual.getPriority()).isEqualTo(MEDIUM);
    }
}
