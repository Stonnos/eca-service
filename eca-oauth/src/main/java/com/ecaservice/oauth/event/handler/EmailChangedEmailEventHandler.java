package com.ecaservice.oauth.event.handler;

import com.ecaservice.oauth.event.model.EmailChangedEmailEvent;
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
}
