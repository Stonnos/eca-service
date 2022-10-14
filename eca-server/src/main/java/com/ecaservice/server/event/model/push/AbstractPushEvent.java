package com.ecaservice.server.event.model.push;

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
    public AbstractPushEvent(Object source) {
        super(source);
    }
}
