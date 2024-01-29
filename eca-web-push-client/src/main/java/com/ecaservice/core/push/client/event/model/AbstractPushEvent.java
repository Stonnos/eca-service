package com.ecaservice.core.push.client.event.model;

import org.springframework.context.ApplicationEvent;

/**
 * Abstract push event
 *
 * @author Roman Batygin
 */
public abstract class AbstractPushEvent extends ApplicationEvent {

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    protected AbstractPushEvent(Object source) {
        super(source);
    }
}
