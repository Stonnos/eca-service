package com.ecaservice.oauth.event.model;

import org.springframework.context.ApplicationEvent;

/**
 * User profile options data event.
 *
 * @author Roman Batygin
 */
public class UserProfileOptionsDataEvent extends ApplicationEvent {

    /**
     * Create a new {@code UserProfileOptionsDataEvent}.
     *
     * @param source - the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public UserProfileOptionsDataEvent(Object source) {
        super(source);
    }
}
