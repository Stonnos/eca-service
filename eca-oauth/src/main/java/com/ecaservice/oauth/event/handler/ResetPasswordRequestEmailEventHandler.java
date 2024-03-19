package com.ecaservice.oauth.event.handler;

import com.ecaservice.oauth.config.AppProperties;
import com.ecaservice.oauth.event.model.ResetPasswordRequestEmailEvent;
import com.ecaservice.oauth.service.mail.dictionary.Templates;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.ecaservice.oauth.service.mail.dictionary.TemplateVariablesDictionary.RESET_PASSWORD_URL_KEY;
import static com.ecaservice.oauth.service.mail.dictionary.TemplateVariablesDictionary.VALIDITY_MINUTES_KEY;
import static com.google.common.collect.Maps.newHashMap;

/**
 * Implements reset password email event handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class ResetPasswordRequestEmailEventHandler
        extends AbstractTokenEmailEventHandler<ResetPasswordRequestEmailEvent> {

    private final AppProperties appProperties;

    /**
     * Creates reset password notification event handler.
     *
     * @param appProperties - app properties
     */
    public ResetPasswordRequestEmailEventHandler(AppProperties appProperties) {
        super(ResetPasswordRequestEmailEvent.class);
        this.appProperties = appProperties;
    }

    @Override
    public String getTemplateCode(ResetPasswordRequestEmailEvent emailEvent) {
        return Templates.RESET_PASSWORD;
    }

    @Override
    public Map<String, String> createVariables(ResetPasswordRequestEmailEvent event) {
        Map<String, String> templateVariables = newHashMap();
        String tokenEndpoint =
                String.format(appProperties.getResetPassword().getUrl(), event.getTokenModel().getToken());
        String resetPasswordUrl = String.format("%s%s", appProperties.getWebExternalBaseUrl(), tokenEndpoint);
        templateVariables.put(RESET_PASSWORD_URL_KEY, resetPasswordUrl);
        templateVariables.put(VALIDITY_MINUTES_KEY,
                String.valueOf(appProperties.getResetPassword().getValidityMinutes()));
        return templateVariables;
    }
}
