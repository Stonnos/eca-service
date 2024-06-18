package com.ecaservice.oauth.event.handler;

import com.ecaservice.notification.dto.EmailRequest;
import com.ecaservice.oauth.config.AppProperties;
import com.ecaservice.oauth.event.model.ResetPasswordRequestEmailEvent;
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

import static com.ecaservice.notification.util.Priority.MEDIUM;
import static com.ecaservice.oauth.TestHelperUtils.createResetPasswordRequestEntity;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for class {@link ResetPasswordRequestEmailEventHandler}.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties
@TestPropertySource("classpath:application.properties")
@Import({AppProperties.class, ResetPasswordRequestEmailEventHandler.class})
class ResetPasswordRequestEmailEventHandlerTest {

    private static final String TOKEN = "token";
    private static final long USER_ID = 1L;

    @Autowired
    private AppProperties appProperties;
    @Autowired
    private ResetPasswordRequestEmailEventHandler eventHandler;

    @Test
    void testEvent() {
        var resetPasswordRequestEntity = createResetPasswordRequestEntity();
        resetPasswordRequestEntity.getUserEntity().setId(USER_ID);
        var tokenModel = TokenModel.builder()
                .token(TOKEN)
                .tokenId(resetPasswordRequestEntity.getId())
                .login(resetPasswordRequestEntity.getUserEntity().getLogin())
                .email(resetPasswordRequestEntity.getUserEntity().getEmail())
                .build();
        ;
        var resetPasswordNotificationEvent = new ResetPasswordRequestEmailEvent(this, tokenModel);
        EmailRequest actual = eventHandler.handle(resetPasswordNotificationEvent);
        assertThat(actual).isNotNull();
        assertThat(actual.getTemplateCode()).isEqualTo(Templates.RESET_PASSWORD);
        assertThat(actual.getReceiver()).isEqualTo(resetPasswordRequestEntity.getUserEntity().getEmail());
        assertThat(actual.getVariables()).isNotEmpty();
        assertThat(actual.getVariables()).containsEntry(TemplateVariablesDictionary.VALIDITY_MINUTES_KEY,
                String.valueOf(appProperties.getResetPassword().getValidityMinutes()));
        String tokenEndpoint = String.format(appProperties.getResetPassword().getUrl(), TOKEN);
        String expectedUrl = String.format("%s%s", appProperties.getWebExternalBaseUrl(), tokenEndpoint);
        assertThat(actual.getVariables()).containsEntry(TemplateVariablesDictionary.RESET_PASSWORD_URL_KEY,
                expectedUrl);
        assertThat(actual.getPriority()).isEqualTo(MEDIUM);
    }
}
