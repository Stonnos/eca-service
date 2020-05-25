package com.ecaservice.oauth.event.model;

import com.ecaservice.oauth.entity.UserEntity;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * User created event.
 *
 * @author Roman Batygin
 */
public class UserCreatedEvent extends ApplicationEvent {

    /**
     * User entity
     */
    @Getter
    private final UserEntity userEntity;

    /**
     * User password
     */
    @Getter
    private final String password;

    /**
     * Create a new {@code UserCreatedEvent}.
     *
     * @param source     the object on which the event initially occurred or with
     *                   which the event is associated (never {@code null})
     * @param userEntity - user entity
     * @param password   - user password
     */
    public UserCreatedEvent(Object source, UserEntity userEntity, String password) {
        super(source);
        this.userEntity = userEntity;
        this.password = password;
    }
}
