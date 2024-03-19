package com.ecaservice.core.push.client.event.model;

/**
 * Abstract system push event
 *
 * @author Roman Batygin
 */
public abstract class AbstractSystemPushEvent extends AbstractPushEvent {

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    protected AbstractSystemPushEvent(Object source) {
        super(source);
    }
}
