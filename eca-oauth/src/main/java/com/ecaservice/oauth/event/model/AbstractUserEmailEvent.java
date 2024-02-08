package com.ecaservice.oauth.event.model;

import com.ecaservice.core.mail.client.event.model.AbstractEmailEvent;
import com.ecaservice.oauth.entity.UserEntity;
import lombok.Getter;

/**
 * Abstract user email event.
 *
 * @author Roman Batygin
 */
public abstract class AbstractUserEmailEvent extends AbstractEmailEvent {

    /**
     * User entity
     */
    @Getter
    private final UserEntity userEntity;

    /**
     * Create a new event.
     *
     * @param source     the object on which the event initially occurred or with
     *                   which the event is associated (never {@code null})
     * @param userEntity - user entity
     */
    protected AbstractUserEmailEvent(Object source, UserEntity userEntity) {
        super(source);
        this.userEntity = userEntity;
    }
}
