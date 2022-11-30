package com.ecaservice.server.event.model.mail;

import com.ecaservice.core.mail.client.event.model.AbstractEmailEvent;
import com.ecaservice.server.model.entity.Experiment;
import lombok.Getter;

/**
 * Abstract experiment email event.
 *
 * @author Roman Batygin
 */
public abstract class AbstractExperimentEmailEvent extends AbstractEmailEvent {

    /**
     * Experiment entity
     */
    @Getter
    private final Experiment experiment;

    /**
     * Create a new event.
     *
     * @param source     - the object on which the event initially occurred (never {@code null})
     * @param experiment - experiment entity
     */
    public AbstractExperimentEmailEvent(Object source, Experiment experiment) {
        super(source);
        this.experiment = experiment;
    }
}
