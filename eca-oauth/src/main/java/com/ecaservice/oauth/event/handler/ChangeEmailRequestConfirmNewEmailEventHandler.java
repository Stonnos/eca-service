package com.ecaservice.oauth.event.handler;

import com.ecaservice.oauth.config.AppProperties;
import com.ecaservice.oauth.event.model.ChangeEmailRequestConfirmNewEmailEvent;
import com.ecaservice.oauth.service.mail.dictionary.Templates;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.ecaservice.oauth.service.mail.dictionary.TemplateVariablesDictionary.CONFIRMATION_CODE_KEY;
import static com.ecaservice.oauth.service.mail.dictionary.TemplateVariablesDictionary.VALIDITY_MINUTES_KEY;
import static com.google.common.collect.Maps.newHashMap;

/**
 * Implements change email request new email event handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class ChangeEmailRequestConfirmNewEmailEventHandler
        extends AbstractTokenEmailEventHandler<ChangeEmailRequestConfirmNewEmailEvent> {

    private final AppProperties appProperties;

    /**
     * Creates change email request new email notification event handler.
     *
     * @param appProperties - app properties
     */
    public ChangeEmailRequestConfirmNewEmailEventHandler(AppProperties appProperties) {
        super(ChangeEmailRequestConfirmNewEmailEvent.class);
        this.appProperties = appProperties;
    }

    @Override
    public String getTemplateCode(ChangeEmailRequestConfirmNewEmailEvent emailEvent) {
        return Templates.CHANGE_EMAIL_CONFIRM_NEW_EMAIL;
    }

    @Override
    public String getCorrelationId(ChangeEmailRequestConfirmNewEmailEvent emailEvent) {
        return emailEvent.getTokenModel().getToken();
    }

    @Override
    public String getReceiver(ChangeEmailRequestConfirmNewEmailEvent event) {
        return event.getNewEmail();
    }

    @Override
    public Map<String, String> createVariables(ChangeEmailRequestConfirmNewEmailEvent event) {
        Long validityMinutes = appProperties.getChangeEmail().getValidityMinutes();
        Map<String, String> templateVariables = newHashMap();
        templateVariables.put(CONFIRMATION_CODE_KEY, event.getTokenModel().getConfirmationCode());
        templateVariables.put(VALIDITY_MINUTES_KEY, String.valueOf(validityMinutes));
        return templateVariables;
    }
}
