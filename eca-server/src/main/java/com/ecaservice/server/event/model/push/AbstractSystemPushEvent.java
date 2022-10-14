package com.ecaservice.server.event.model.push;

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
    public AbstractSystemPushEvent(Object source) {
        super(source);
    }
}
