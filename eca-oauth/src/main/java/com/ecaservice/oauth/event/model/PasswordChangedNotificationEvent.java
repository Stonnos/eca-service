package com.ecaservice.oauth.event.model;

import com.ecaservice.oauth.entity.UserEntity;

/**
 * Password changed notification event.
 *
 * @author Roman Batygin
 */
public class PasswordChangedNotificationEvent extends AbstractUserNotificationEvent {

    /**
     * Create a new {@code PasswordChangedNotificationEvent}.
     *
     * @param source     - the object on which the event initially occurred or with which the event is
     *                   associated (never {@code null})
     * @param userEntity - user entity
     */
    public PasswordChangedNotificationEvent(Object source, UserEntity userEntity) {
        super(source, userEntity);
    }
}
