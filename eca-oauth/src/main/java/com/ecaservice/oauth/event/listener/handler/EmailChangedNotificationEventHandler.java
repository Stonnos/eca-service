package com.ecaservice.oauth.event.listener.handler;

import com.ecaservice.oauth.event.model.EmailChangedNotificationEvent;
import com.ecaservice.oauth.service.mail.dictionary.Templates;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Email changed notification event handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class EmailChangedNotificationEventHandler extends AbstractNotificationEventHandler<EmailChangedNotificationEvent> {

    /**
     * Creates email changed notification event handler.
     */
    public EmailChangedNotificationEventHandler() {
        super(EmailChangedNotificationEvent.class, Templates.EMAIL_CHANGED);
    }
}
