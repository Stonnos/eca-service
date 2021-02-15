package com.ecaservice.event.model;

import com.ecaservice.model.entity.Experiment;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Experiment change status event.
 *
 * @author Roman Batygin
 */
public class ExperimentChangeStatusEvent extends ApplicationEvent {

    /**
     * Experiment entity
     */
    @Getter
    private final Experiment experiment;

    /**
     * Create a new ExperimentChangeStatusEvent.
     *
     * @param source     - the object on which the event initially occurred (never {@code null})
     * @param experiment - experiment entity
     */
    public ExperimentChangeStatusEvent(Object source, Experiment experiment) {
        super(source);
        this.experiment = experiment;
    }
}
