package com.ecaservice.oauth.event.handler;

import com.ecaservice.oauth.event.model.PasswordChangedEmailEvent;
import com.ecaservice.oauth.service.mail.dictionary.Templates;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Password changed email event handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class PasswordChangedEmailEventHandler
        extends AbstractUserEmailEventHandler<PasswordChangedEmailEvent> {

    /**
     * Creates password changed notification event handler.
     */
    public PasswordChangedEmailEventHandler() {
        super(PasswordChangedEmailEvent.class);
    }

    @Override
    public String getTemplateCode(PasswordChangedEmailEvent emailEvent) {
        return Templates.PASSWORD_CHANGED;
    }
}
