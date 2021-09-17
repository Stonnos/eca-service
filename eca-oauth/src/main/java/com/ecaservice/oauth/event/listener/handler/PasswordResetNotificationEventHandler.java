package com.ecaservice.oauth.event.listener.handler;

import com.ecaservice.oauth.event.model.PasswordResetNotificationEvent;
import com.ecaservice.oauth.service.mail.dictionary.Templates;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Password reset notification event handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class PasswordResetNotificationEventHandler
        extends AbstractNotificationEventHandler<PasswordResetNotificationEvent> {

    /**
     * Creates password reset notification event handler.
     */
    public PasswordResetNotificationEventHandler() {
        super(PasswordResetNotificationEvent.class, Templates.PASSWORD_RESET);
    }
}
