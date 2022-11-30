package com.ecaservice.server.event.model.mail;

import com.ecaservice.server.model.entity.Experiment;

/**
 * Error experiment email event.
 *
 * @author Roman Batygin
 */
public class ErrorExperimentEmailEvent extends AbstractExperimentEmailEvent {

    /**
     * Create a new event.
     *
     * @param source     - the object on which the event initially occurred (never {@code null})
     * @param experiment - experiment entity
     */
    public ErrorExperimentEmailEvent(Object source, Experiment experiment) {
        super(source, experiment);
    }
}
