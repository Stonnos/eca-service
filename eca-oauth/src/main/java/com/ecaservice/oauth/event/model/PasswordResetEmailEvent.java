package com.ecaservice.oauth.event.model;

import com.ecaservice.oauth.entity.UserEntity;

/**
 * Password reset email event.
 *
 * @author Roman Batygin
 */
public class PasswordResetEmailEvent extends AbstractUserEmailEvent {

    /**
     * Create a new event.
     *
     * @param source     - the object on which the event initially occurred or with which the event is
     *                   associated (never {@code null})
     * @param userEntity - user entity
     */
    public PasswordResetEmailEvent(Object source, UserEntity userEntity) {
        super(source, userEntity);
    }
}
