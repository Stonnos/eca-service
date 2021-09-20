package com.ecaservice.oauth.event.model;

import org.springframework.context.ApplicationEvent;

/**
 * Abstract notification event.
 *
 * @author Roman Batygin
 */
public abstract class AbstractNotificationEvent extends ApplicationEvent {

    /**
     * Create a new {@code AbstractNotificationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    protected AbstractNotificationEvent(Object source) {
        super(source);
    }

    /**
     * Gets receiver.
     *
     * @return receiver
     */
    public abstract String getReceiver();
}
