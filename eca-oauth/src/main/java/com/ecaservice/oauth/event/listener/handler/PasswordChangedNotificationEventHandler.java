package com.ecaservice.oauth.event.listener.handler;

import com.ecaservice.oauth.event.model.PasswordChangedNotificationEvent;
import com.ecaservice.oauth.service.mail.dictionary.Templates;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Password changed notification event handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class PasswordChangedNotificationEventHandler
        extends AbstractNotificationEventHandler<PasswordChangedNotificationEvent> {

    /**
     * Creates password changed notification event handler.
     */
    public PasswordChangedNotificationEventHandler() {
        super(PasswordChangedNotificationEvent.class, Templates.PASSWORD_CHANGED);
    }
}
