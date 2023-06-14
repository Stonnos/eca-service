package com.ecaservice.server.event.model.push;

import com.ecaservice.server.model.entity.Experiment;
import lombok.Getter;

/**
 * Experiment system push event.
 *
 * @author Roman Batygin
 */
public class ExperimentSystemPushEvent extends AbstractSystemPushEvent {

    /**
     * Experiment entity
     */
    @Getter
    private final Experiment experiment;

    /**
     * Create a new ExperimentSystemPushEvent.
     *
     * @param source     - the object on which the event initially occurred (never {@code null})
     * @param experiment - experiment entity
     */
    public ExperimentSystemPushEvent(Object source, Experiment experiment) {
        super(source);
        this.experiment = experiment;
    }
}
