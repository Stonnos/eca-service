package com.ecaservice.oauth.event.model;

import com.ecaservice.oauth.entity.UserEntity;

/**
 * Email changed email event.
 *
 * @author Roman Batygin
 */
public class EmailChangedEmailEvent extends AbstractUserEmailEvent {

    /**
     * Create a new event.
     *
     * @param source     - the object on which the event initially occurred or with which the event is
     *                   associated (never {@code null})
     * @param userEntity - user entity
     */
    public EmailChangedEmailEvent(Object source, UserEntity userEntity) {
        super(source, userEntity);
    }
}
