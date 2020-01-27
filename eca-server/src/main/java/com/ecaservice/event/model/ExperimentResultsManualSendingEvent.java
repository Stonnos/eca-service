package com.ecaservice.event.model;

import com.ecaservice.model.entity.Experiment;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Experiment results manual sending event.
 *
 * @author Roman Batygin
 */
public class ExperimentResultsManualSendingEvent extends ApplicationEvent {

    /**
     * Experiment entity
     */
    @Getter
    private final Experiment experiment;

    /**
     * Create a new ExperimentResultsSendingEvent.
     *
     * @param source     - the object on which the event initially occurred (never {@code null})
     * @param experiment - experiment entity
     */
    public ExperimentResultsManualSendingEvent(Object source, Experiment experiment) {
        super(source);
        this.experiment = experiment;
    }
}
