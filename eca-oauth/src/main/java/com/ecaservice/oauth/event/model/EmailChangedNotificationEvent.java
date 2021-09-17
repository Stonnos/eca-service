package com.ecaservice.oauth.event.model;

import com.ecaservice.oauth.entity.UserEntity;

/**
 * Email changed notification event.
 *
 * @author Roman Batygin
 */
public class EmailChangedNotificationEvent extends AbstractUserNotificationEvent {

    /**
     * Create a new {@code EmailChangedNotificationEvent}.
     *
     * @param source     - the object on which the event initially occurred or with which the event is
     *                   associated (never {@code null})
     * @param userEntity - user entity
     */
    public EmailChangedNotificationEvent(Object source, UserEntity userEntity) {
        super(source, userEntity);
    }
}
