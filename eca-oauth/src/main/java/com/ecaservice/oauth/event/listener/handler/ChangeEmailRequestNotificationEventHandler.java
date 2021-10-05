package com.ecaservice.oauth.event.listener.handler;

import com.ecaservice.oauth.config.AppProperties;
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

    private final AppProperties appProperties;

    /**
     * Creates change email notification event handler.
     *
     * @param appProperties - app properties
     */
    public ChangeEmailRequestNotificationEventHandler(AppProperties appProperties) {
        super(ChangeEmailRequestNotificationEvent.class, Templates.CHANGE_EMAIL);
        this.appProperties = appProperties;
    }

    @Override
    Map<String, String> createVariables(ChangeEmailRequestNotificationEvent event) {
        String tokenEndpoint = String.format(appProperties.getChangeEmail().getUrl(), event.getTokenModel().getToken());
        String changeEmailUrl = String.format("%s%s", appProperties.getWebExternalBaseUrl(), tokenEndpoint);
        Map<String, String> templateVariables = newHashMap();
        templateVariables.put(CHANGE_EMAIL_URL_KEY, changeEmailUrl);
        templateVariables.put(VALIDITY_HOURS_KEY, String.valueOf(appProperties.getChangeEmail().getValidityMinutes()));
        return templateVariables;
    }
}
