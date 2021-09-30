package com.ecaservice.server.event.model;

import com.ecaservice.server.model.entity.Experiment;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Experiment response event.
 *
 * @author Roman Batygin
 */
public class ExperimentResponseEvent extends ApplicationEvent {

    /**
     * Experiment entity
     */
    @Getter
    private final Experiment experiment;

    /**
     * Create a new ExperimentResponseEvent.
     *
     * @param source     - the object on which the event initially occurred (never {@code null})
     * @param experiment - experiment entity
     */
    public ExperimentResponseEvent(Object source, Experiment experiment) {
        super(source);
        this.experiment = experiment;
    }
}
