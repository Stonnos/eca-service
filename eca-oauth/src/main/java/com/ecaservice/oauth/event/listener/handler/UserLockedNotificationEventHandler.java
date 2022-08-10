package com.ecaservice.oauth.event.listener.handler;

import com.ecaservice.oauth.event.model.UserLockedNotificationEvent;
import com.ecaservice.oauth.service.mail.dictionary.Templates;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * User locked notification event handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class UserLockedNotificationEventHandler
        extends AbstractNotificationEventHandler<UserLockedNotificationEvent> {

    /**
     * Creates user locked notification event handler.
     */
    public UserLockedNotificationEventHandler() {
        super(UserLockedNotificationEvent.class, Templates.USER_LOCKED);
    }
}
