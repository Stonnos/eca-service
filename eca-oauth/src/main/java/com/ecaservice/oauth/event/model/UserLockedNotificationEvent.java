package com.ecaservice.oauth.event.model;

import com.ecaservice.oauth.entity.UserEntity;

/**
 * User locked notification event.
 *
 * @author Roman Batygin
 */
public class UserLockedNotificationEvent extends AbstractUserNotificationEvent {

    /**
     * Create a new {@code UserLockedNotificationEvent}.
     *
     * @param source     - the object on which the event initially occurred or with which the event is
     *                   associated (never {@code null})
     * @param userEntity - user entity
     */
    public UserLockedNotificationEvent(Object source, UserEntity userEntity) {
        super(source, userEntity);
    }
}
