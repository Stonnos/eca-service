package com.ecaservice.server.event.model;

import com.ecaservice.server.model.entity.Experiment;
import eca.dataminer.AbstractExperiment;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Experiment ERS report event.
 *
 * @author Roman Batygin
 */
public class ExperimentErsReportEvent extends ApplicationEvent {

    /**
     * Experiment entity
     */
    @Getter
    private final Experiment experiment;

    /**
     * Experiment history
     */
    @Getter
    private final AbstractExperiment<?> experimentHistory;

    /**
     * Create a new event.
     *
     * @param source            - the object on which the event initially occurred (never {@code null})
     * @param experiment        - experiment entity
     * @param experimentHistory - experiment history
     */
    public ExperimentErsReportEvent(Object source, Experiment experiment, AbstractExperiment<?> experimentHistory) {
        super(source);
        this.experiment = experiment;
        this.experimentHistory = experimentHistory;
    }
}
