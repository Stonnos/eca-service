package com.ecaservice.oauth.event.handler;

import com.ecaservice.oauth.config.AppProperties;
import com.ecaservice.oauth.event.model.ChangeEmailRequestEmailEvent;
import com.ecaservice.oauth.service.mail.dictionary.Templates;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.ecaservice.oauth.service.mail.dictionary.TemplateVariablesDictionary.NEW_EMAIL;
import static com.ecaservice.oauth.service.mail.dictionary.TemplateVariablesDictionary.REVOKE_CHANGE_EMAIL_REQUEST_URL;
import static com.google.common.collect.Maps.newHashMap;

/**
 * Implements change email event handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class ChangeEmailRequestEmailEventHandler
        extends AbstractTokenEmailEventHandler<ChangeEmailRequestEmailEvent> {

    private final AppProperties appProperties;

    /**
     * Creates change email notification event handler.
     *
     * @param appProperties - app properties
     */
    public ChangeEmailRequestEmailEventHandler(AppProperties appProperties) {
        super(ChangeEmailRequestEmailEvent.class);
        this.appProperties = appProperties;
    }

    @Override
    public String getTemplateCode(ChangeEmailRequestEmailEvent emailEvent) {
        return Templates.CHANGE_EMAIL;
    }

    @Override
    public String getCorrelationId(ChangeEmailRequestEmailEvent emailEvent) {
        return emailEvent.getTokenModel().getToken();
    }

    @Override
    public Map<String, String> createVariables(ChangeEmailRequestEmailEvent event) {
        Map<String, String> templateVariables = newHashMap();
        String revokeEndpoint = String.format(appProperties.getChangeEmail().getRevocationUrl(),
                event.getTokenModel().getRevocationToken());
        String revokeChangeEmailUrl = String.format("%s%s", appProperties.getWebExternalBaseUrl(), revokeEndpoint);
        templateVariables.put(NEW_EMAIL, event.getNewEmail());
        templateVariables.put(REVOKE_CHANGE_EMAIL_REQUEST_URL, revokeChangeEmailUrl);
        return templateVariables;
    }
}
