package com.ecaservice.oauth.event.listener.handler;

import com.ecaservice.oauth.config.ResetPasswordConfig;
import com.ecaservice.oauth.event.model.ResetPasswordRequestNotificationEvent;
import com.ecaservice.oauth.service.mail.dictionary.Templates;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.ecaservice.oauth.service.mail.dictionary.TemplateVariablesDictionary.RESET_PASSWORD_URL_KEY;
import static com.ecaservice.oauth.service.mail.dictionary.TemplateVariablesDictionary.VALIDITY_MINUTES_KEY;
import static com.google.common.collect.Maps.newHashMap;

/**
 * Implements reset password notification event handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class ResetPasswordRequestNotificationEventHandler
        extends AbstractNotificationEventHandler<ResetPasswordRequestNotificationEvent> {

    private static final String RESET_PASSWORD_URL_FORMAT = "%s/reset-password/?token=%s";

    private final ResetPasswordConfig resetPasswordConfig;

    /**
     * Creates reset password notification event handler.
     *
     * @param resetPasswordConfig - reset password config
     */
    public ResetPasswordRequestNotificationEventHandler(ResetPasswordConfig resetPasswordConfig) {
        super(ResetPasswordRequestNotificationEvent.class, Templates.RESET_PASSWORD);
        this.resetPasswordConfig = resetPasswordConfig;
    }

    @Override
    Map<String, String> createVariables(ResetPasswordRequestNotificationEvent event) {
        Map<String, String> templateVariables = newHashMap();
        String resetPasswordUrl = String.format(RESET_PASSWORD_URL_FORMAT, resetPasswordConfig.getBaseUrl(),
                event.getTokenModel().getToken());
        templateVariables.put(RESET_PASSWORD_URL_KEY, resetPasswordUrl);
        templateVariables.put(VALIDITY_MINUTES_KEY, String.valueOf(resetPasswordConfig.getValidityMinutes()));
        return templateVariables;
    }
}
