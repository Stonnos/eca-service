package com.ecaservice.oauth.event.handler;

import com.ecaservice.oauth.event.model.UserUnLockedEmailEvent;
import com.ecaservice.oauth.service.mail.dictionary.Templates;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * User unlocked email event handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class UserUnLockedEmailEventHandler
        extends AbstractUserEmailEventHandler<UserUnLockedEmailEvent> {

    /**
     * Creates user unlocked notification event handler.
     */
    public UserUnLockedEmailEventHandler() {
        super(UserUnLockedEmailEvent.class);
    }

    @Override
    public String getTemplateCode(UserUnLockedEmailEvent emailEvent) {
        return Templates.USER_UNLOCKED;
    }
}
