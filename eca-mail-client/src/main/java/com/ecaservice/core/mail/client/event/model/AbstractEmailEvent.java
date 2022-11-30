package com.ecaservice.core.mail.client.event.model;

import org.springframework.context.ApplicationEvent;

/**
 * Abstract email event.
 *
 * @author Roman Batygin
 */
public abstract class AbstractEmailEvent extends ApplicationEvent {

    /**
     * Create a new {@code AbstractNotificationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    protected AbstractEmailEvent(Object source) {
        super(source);
    }

    /**
     * Gets receiver.
     *
     * @return receiver
     */
    public abstract String getReceiver();
}
