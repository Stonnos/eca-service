package com.ecaservice.oauth.event.handler;

import com.ecaservice.oauth.event.model.EmailChangedEmailEvent;
import com.ecaservice.oauth.service.mail.dictionary.Templates;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.ecaservice.oauth.service.mail.dictionary.TemplateVariablesDictionary.NEW_EMAIL;
import static com.google.common.collect.Maps.newHashMap;

/**
 * Email changed email event handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class EmailChangedEmailEventHandler
        extends AbstractUserEmailEventHandler<EmailChangedEmailEvent> {

    /**
     * Creates email changed notification event handler.
     */
    public EmailChangedEmailEventHandler() {
        super(EmailChangedEmailEvent.class);
    }

    @Override
    public String getTemplateCode(EmailChangedEmailEvent emailEvent) {
        return Templates.EMAIL_CHANGED;
    }

    @Override
    public String getCorrelationId(EmailChangedEmailEvent emailEvent) {
        return emailEvent.getChangeEmailRequestEntity().getToken();
    }

    @Override
    public String getReceiver(EmailChangedEmailEvent event) {
        return event.getChangeEmailRequestEntity().getOldEmail();
    }

    @Override
    public Map<String, String> createVariables(EmailChangedEmailEvent event) {
        Map<String, String> templateVariables = newHashMap();
        templateVariables.put(NEW_EMAIL, event.getChangeEmailRequestEntity().getNewEmail());
        return templateVariables;
    }
}
