package com.ecaservice.event.model;

import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.experiment.ExperimentResultsRequestSource;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Experiment results sending event.
 *
 * @author Roman Batygin
 */
public class ExperimentResultsSendingEvent extends ApplicationEvent {

    /**
     * Experiment entity
     */
    @Getter
    private final Experiment experiment;

    /**
     * Experiment results request source
     */
    @Getter
    private ExperimentResultsRequestSource experimentResultsRequestSource;

    /**
     * Create a new ExperimentResultsSendingEvent.
     *
     * @param source                         - the object on which the event initially occurred (never {@code null})
     * @param experiment                     - experiment entity
     * @param experimentResultsRequestSource - experiment results request source
     */
    public ExperimentResultsSendingEvent(Object source, Experiment experiment,
                                         ExperimentResultsRequestSource experimentResultsRequestSource) {
        super(source);
        this.experiment = experiment;
        this.experimentResultsRequestSource = experimentResultsRequestSource;
    }
}
