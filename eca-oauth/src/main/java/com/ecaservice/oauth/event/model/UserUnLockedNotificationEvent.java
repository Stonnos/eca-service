package com.ecaservice.oauth.event.model;

import com.ecaservice.oauth.entity.UserEntity;

/**
 * User unlocked notification event.
 *
 * @author Roman Batygin
 */
public class UserUnLockedNotificationEvent extends AbstractUserNotificationEvent {

    /**
     * Create a new {@code UserUnLockedNotificationEvent}.
     *
     * @param source     - the object on which the event initially occurred or with which the event is
     *                   associated (never {@code null})
     * @param userEntity - user entity
     */
    public UserUnLockedNotificationEvent(Object source, UserEntity userEntity) {
        super(source, userEntity);
    }
}
