package com.ecaservice.oauth.event.model;

import com.ecaservice.oauth.entity.UserEntity;

/**
 * Password reset notification event.
 *
 * @author Roman Batygin
 */
public class PasswordResetNotificationEvent extends AbstractUserNotificationEvent {

    /**
     * Create a new {@code PasswordResetNotificationEvent}.
     *
     * @param source     - the object on which the event initially occurred or with which the event is
     *                   associated (never {@code null})
     * @param userEntity - user entity
     */
    public PasswordResetNotificationEvent(Object source, UserEntity userEntity) {
        super(source, userEntity);
    }
}
