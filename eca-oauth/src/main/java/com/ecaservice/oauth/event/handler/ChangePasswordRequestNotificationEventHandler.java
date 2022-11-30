package com.ecaservice.oauth.event.handler;

import com.ecaservice.oauth.config.AppProperties;
import com.ecaservice.oauth.event.model.ChangePasswordRequestNotificationEvent;
import com.ecaservice.oauth.service.mail.dictionary.Templates;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.ecaservice.oauth.service.mail.dictionary.TemplateVariablesDictionary.CHANGE_PASSWORD_URL_KEY;
import static com.ecaservice.oauth.service.mail.dictionary.TemplateVariablesDictionary.VALIDITY_MINUTES_KEY;
import static com.google.common.collect.Maps.newHashMap;

/**
 * Implements change password notification event handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class ChangePasswordRequestNotificationEventHandler
        extends AbstractTokenNotificationEventHandler<ChangePasswordRequestNotificationEvent> {

    private final AppProperties appProperties;

    /**
     * Creates change password notification event handler.
     *
     * @param appProperties - app properties
     */
    public ChangePasswordRequestNotificationEventHandler(AppProperties appProperties) {
        super(ChangePasswordRequestNotificationEvent.class, Templates.CHANGE_PASSWORD);
        this.appProperties = appProperties;
    }

    @Override
    public Map<String, String> createVariables(ChangePasswordRequestNotificationEvent event) {
        String tokenEndpoint =
                String.format(appProperties.getChangePassword().getUrl(), event.getTokenModel().getToken());
        String changePasswordUrl = String.format("%s%s", appProperties.getWebExternalBaseUrl(), tokenEndpoint);
        Map<String, String> templateVariables = newHashMap();
        templateVariables.put(CHANGE_PASSWORD_URL_KEY, changePasswordUrl);
        templateVariables.put(VALIDITY_MINUTES_KEY,
                String.valueOf(appProperties.getChangePassword().getValidityMinutes()));
        return templateVariables;
    }
}
