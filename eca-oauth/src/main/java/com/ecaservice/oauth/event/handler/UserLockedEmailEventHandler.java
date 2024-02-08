package com.ecaservice.oauth.event.handler;

import com.ecaservice.oauth.event.model.UserLockedEmailEvent;
import com.ecaservice.oauth.service.mail.dictionary.Templates;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * User locked email event handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class UserLockedEmailEventHandler
        extends AbstractUserEmailEventHandler<UserLockedEmailEvent> {

    /**
     * Creates user locked notification event handler.
     */
    public UserLockedEmailEventHandler() {
        super(UserLockedEmailEvent.class);
    }

    @Override
    public String getTemplateCode(UserLockedEmailEvent emailEvent) {
        return Templates.USER_LOCKED;
    }
}
