package com.ecaservice.oauth.event.handler;

import com.ecaservice.oauth.config.AppProperties;
import com.ecaservice.oauth.event.model.ChangeEmailRequestNotificationEvent;
import com.ecaservice.oauth.service.mail.dictionary.Templates;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.ecaservice.oauth.service.mail.dictionary.TemplateVariablesDictionary.CONFIRMATION_CODE_KEY;
import static com.ecaservice.oauth.service.mail.dictionary.TemplateVariablesDictionary.NEW_EMAIL;
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
        extends AbstractTokenNotificationEventHandler<ChangeEmailRequestNotificationEvent> {

    private static final long MINUTES_IN_HOUR = 60L;

    private final AppProperties appProperties;

    /**
     * Creates change email notification event handler.
     *
     * @param appProperties - app properties
     */
    public ChangeEmailRequestNotificationEventHandler(AppProperties appProperties) {
        super(ChangeEmailRequestNotificationEvent.class);
        this.appProperties = appProperties;
    }

    @Override
    public String getTemplateCode(ChangeEmailRequestNotificationEvent emailEvent) {
        return Templates.CHANGE_EMAIL;
    }

    @Override
    public Map<String, String> createVariables(ChangeEmailRequestNotificationEvent event) {
        Long validityHours = appProperties.getChangeEmail().getValidityMinutes() / MINUTES_IN_HOUR;
        Map<String, String> templateVariables = newHashMap();
        templateVariables.put(CONFIRMATION_CODE_KEY, event.getTokenModel().getConfirmationCode());
        templateVariables.put(NEW_EMAIL, event.getNewEmail());
        templateVariables.put(VALIDITY_HOURS_KEY, String.valueOf(validityHours));
        return templateVariables;
    }
}
