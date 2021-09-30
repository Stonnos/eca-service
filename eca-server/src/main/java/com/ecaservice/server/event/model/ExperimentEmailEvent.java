package com.ecaservice.server.event.model;

import com.ecaservice.server.model.entity.Experiment;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Experiment email event.
 *
 * @author Roman Batygin
 */
public class ExperimentEmailEvent extends ApplicationEvent {

    /**
     * Experiment entity
     */
    @Getter
    private final Experiment experiment;

    /**
     * Create a new ExperimentEmailEvent.
     *
     * @param source     - the object on which the event initially occurred (never {@code null})
     * @param experiment - experiment entity
     */
    public ExperimentEmailEvent(Object source, Experiment experiment) {
        super(source);
        this.experiment = experiment;
    }
}
