package com.ecaservice.oauth.event.handler;

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
        extends AbstractUserNotificationEventHandler<PasswordChangedNotificationEvent> {

    /**
     * Creates password changed notification event handler.
     */
    public PasswordChangedNotificationEventHandler() {
        super(PasswordChangedNotificationEvent.class);
    }

    @Override
    public String getTemplateCode(PasswordChangedNotificationEvent emailEvent) {
        return Templates.PASSWORD_CHANGED;
    }
}
