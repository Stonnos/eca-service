package com.ecaservice.oauth.event.listener.handler;

import com.ecaservice.oauth.event.model.UserUnLockedNotificationEvent;
import com.ecaservice.oauth.service.mail.dictionary.Templates;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * User unlocked notification event handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class UserUnLockedNotificationEventHandler
        extends AbstractNotificationEventHandler<UserUnLockedNotificationEvent> {

    /**
     * Creates user unlocked notification event handler.
     */
    public UserUnLockedNotificationEventHandler() {
        super(UserUnLockedNotificationEvent.class, Templates.USER_UNLOCKED);
    }
}
