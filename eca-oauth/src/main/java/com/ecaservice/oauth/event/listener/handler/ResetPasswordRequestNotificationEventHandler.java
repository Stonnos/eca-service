package com.ecaservice.oauth.event.listener.handler;

import com.ecaservice.oauth.config.AppProperties;
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

    private final AppProperties appProperties;

    /**
     * Creates reset password notification event handler.
     *
     * @param appProperties - app properties
     */
    public ResetPasswordRequestNotificationEventHandler(AppProperties appProperties) {
        super(ResetPasswordRequestNotificationEvent.class, Templates.RESET_PASSWORD);
        this.appProperties = appProperties;
    }

    @Override
    Map<String, String> createVariables(ResetPasswordRequestNotificationEvent event) {
        Map<String, String> templateVariables = newHashMap();
        String tokenEndpoint =
                String.format(appProperties.getResetPassword().getUrl(), event.getTokenModel().getToken());
        String resetPasswordUrl = String.format("%s%s", appProperties.getWebExternalBaseUrl(), tokenEndpoint);
        templateVariables.put(RESET_PASSWORD_URL_KEY, resetPasswordUrl);
        templateVariables.put(VALIDITY_MINUTES_KEY,
                String.valueOf(appProperties.getResetPassword().getValidityMinutes()));
        return templateVariables;
    }

    @Override
    public Long getRequestCacheDurationInMinutes() {
        return appProperties.getResetPassword().getValidityMinutes();
    }
}
