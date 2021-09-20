package com.ecaservice.oauth.event.listener.handler;

import com.ecaservice.oauth.config.ChangeEmailConfig;
import com.ecaservice.oauth.event.model.ChangeEmailRequestNotificationEvent;
import com.ecaservice.oauth.service.mail.dictionary.Templates;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.ecaservice.oauth.service.mail.dictionary.TemplateVariablesDictionary.CHANGE_EMAIL_URL_KEY;
import static com.ecaservice.oauth.service.mail.dictionary.TemplateVariablesDictionary.VALIDITY_HOURS_KEY;
import static com.google.common.collect.Maps.newHashMap;

/**
 * Implements change email notification event handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class ChangeEmailRequestNotificationEventHandler
        extends AbstractNotificationEventHandler<ChangeEmailRequestNotificationEvent> {

    private static final String CHANGE_EMAIL_URL_FORMAT = "%s/change-email/?token=%s";

    private final ChangeEmailConfig changeEmailConfig;

    /**
     * Creates change email notification event handler.
     *
     * @param changeEmailConfig - change email config
     */
    public ChangeEmailRequestNotificationEventHandler(ChangeEmailConfig changeEmailConfig) {
        super(ChangeEmailRequestNotificationEvent.class, Templates.CHANGE_EMAIL);
        this.changeEmailConfig = changeEmailConfig;
    }

    @Override
    Map<String, String> createVariables(ChangeEmailRequestNotificationEvent event) {
        String changePasswordUrl = String.format(CHANGE_EMAIL_URL_FORMAT, changeEmailConfig.getBaseUrl(),
                event.getTokenModel().getToken());
        Map<String, String> templateVariables = newHashMap();
        templateVariables.put(CHANGE_EMAIL_URL_KEY, changePasswordUrl);
        templateVariables.put(VALIDITY_HOURS_KEY, String.valueOf(changeEmailConfig.getValidityHours()));
        return templateVariables;
    }
}
