package com.ecaservice.oauth.event.model;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * User created event.
 *
 * @author Roman Batygin
 */
public class UserCreatedEvent extends ApplicationEvent {

    /**
     * User login
     */
    @Getter
    private final String login;

    /**
     * User password
     */
    @Getter
    private final String password;

    /**
     * Create a new {@code UserCreatedEvent}.
     *
     * @param source   the object on which the event initially occurred or with
     *                 which the event is associated (never {@code null})
     * @param login    - user login
     * @param password - user password
     */
    public UserCreatedEvent(Object source, String login, String password) {
        super(source);
        this.login = login;
        this.password = password;
    }
}
