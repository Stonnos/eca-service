package com.ecaservice.oauth.event.handler;

import com.ecaservice.oauth.event.model.EmailChangedNewEmailConfirmedEvent;
import com.ecaservice.oauth.service.mail.dictionary.Templates;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Email changed email event handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class EmailChangedNewEmailConfirmedEventHandler
        extends AbstractUserEmailEventHandler<EmailChangedNewEmailConfirmedEvent> {

    /**
     * Creates email changed notification event handler.
     */
    public EmailChangedNewEmailConfirmedEventHandler() {
        super(EmailChangedNewEmailConfirmedEvent.class);
    }

    @Override
    public String getTemplateCode(EmailChangedNewEmailConfirmedEvent emailEvent) {
        return Templates.EMAIL_CHANGED_NEW_EMAIL_CONFIRMED;
    }

    @Override
    public String getCorrelationId(EmailChangedNewEmailConfirmedEvent emailEvent) {
        return emailEvent.getChangeEmailRequestEntity().getToken();
    }
}
