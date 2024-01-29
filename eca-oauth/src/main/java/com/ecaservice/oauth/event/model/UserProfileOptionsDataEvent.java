package com.ecaservice.oauth.event.model;

import com.ecaservice.oauth.entity.UserEntity;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * User profile options data event.
 *
 * @author Roman Batygin
 */
public class UserProfileOptionsDataEvent extends ApplicationEvent {

    @Getter
    private final UserEntity userEntity;

    /**
     * Create a new {@code UserProfileOptionsDataEvent}.
     *
     * @param source     - the object on which the event initially occurred or with
     *                   which the event is associated (never {@code null})
     * @param userEntity - user entity
     */
    public UserProfileOptionsDataEvent(Object source, UserEntity userEntity) {
        super(source);
        this.userEntity = userEntity;
    }
}
