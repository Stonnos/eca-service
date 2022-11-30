package com.ecaservice.core.mail.client;

import com.ecaservice.core.mail.client.event.model.AbstractEmailEvent;

/**
 * Test email event.
 *
 * @author Roman Batygin
 */
public class TestEmailEvent extends AbstractEmailEvent {

    /**
     * Create a new {@code AbstractNotificationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public TestEmailEvent(Object source) {
        super(source);
    }
}
