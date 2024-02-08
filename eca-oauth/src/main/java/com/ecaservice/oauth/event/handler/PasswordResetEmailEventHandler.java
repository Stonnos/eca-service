package com.ecaservice.oauth.event.handler;

import com.ecaservice.oauth.event.model.PasswordResetEmailEvent;
import com.ecaservice.oauth.service.mail.dictionary.Templates;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Password reset email event handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class PasswordResetEmailEventHandler
        extends AbstractUserEmailEventHandler<PasswordResetEmailEvent> {

    /**
     * Creates password reset notification event handler.
     */
    public PasswordResetEmailEventHandler() {
        super(PasswordResetEmailEvent.class);
    }

    @Override
    public String getTemplateCode(PasswordResetEmailEvent emailEvent) {
        return Templates.PASSWORD_RESET;
    }
}
