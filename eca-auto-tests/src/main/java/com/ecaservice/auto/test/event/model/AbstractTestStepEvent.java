package com.ecaservice.auto.test.event.model;

import org.springframework.context.ApplicationEvent;

/**
 * Abstract test step event.
 *
 * @author Roman Batygin
 */
public abstract class AbstractTestStepEvent extends ApplicationEvent {

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public AbstractTestStepEvent(Object source) {
        super(source);
    }
}
