package com.ecaservice.server.event.model;

import com.ecaservice.server.model.entity.Experiment;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Experiment web push event.
 *
 * @author Roman Batygin
 */
public class ExperimentWebPushEvent extends ApplicationEvent {

    /**
     * Experiment entity
     */
    @Getter
    private final Experiment experiment;

    /**
     * Create a new ExperimentWebPushEvent.
     *
     * @param source     - the object on which the event initially occurred (never {@code null})
     * @param experiment - experiment entity
     */
    public ExperimentWebPushEvent(Object source, Experiment experiment) {
        super(source);
        this.experiment = experiment;
    }
}
