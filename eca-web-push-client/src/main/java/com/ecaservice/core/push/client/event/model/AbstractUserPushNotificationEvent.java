package com.ecaservice.core.push.client.event.model;

import lombok.Getter;

/**
 * Abstract user push notification event
 *
 * @author Roman Batygin
 */
public abstract class AbstractUserPushNotificationEvent extends AbstractPushEvent {

    @Getter
    private final String initiator;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source    - the object on which the event initially occurred or with which the event
     *                  is associated (never {@code null})
     * @param initiator - initiator (user)
     */
    protected AbstractUserPushNotificationEvent(Object source, String initiator) {
        super(source);
        this.initiator = initiator;
    }
}
