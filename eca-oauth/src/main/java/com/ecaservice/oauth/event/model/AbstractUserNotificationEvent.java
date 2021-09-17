package com.ecaservice.oauth.event.model;

import com.ecaservice.oauth.entity.UserEntity;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Abstract user notification event.
 *
 * @author Roman Batygin
 */
public abstract class AbstractUserNotificationEvent extends ApplicationEvent {

    /**
     * User entity
     */
    @Getter
    private final UserEntity userEntity;

    /**
     * Create a new {@code AbstractNotificationEvent}.
     *
     * @param source     the object on which the event initially occurred or with
     *                   which the event is associated (never {@code null})
     * @param userEntity - user entity
     */
    protected AbstractUserNotificationEvent(Object source, UserEntity userEntity) {
        super(source);
        this.userEntity = userEntity;
    }
}