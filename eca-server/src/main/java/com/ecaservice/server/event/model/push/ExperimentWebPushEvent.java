package com.ecaservice.server.event.model.push;

import com.ecaservice.server.model.entity.Experiment;
import lombok.Getter;

/**
 * Experiment web push event.
 *
 * @author Roman Batygin
 */
public class ExperimentWebPushEvent extends AbstractUserPushNotificationEvent {

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
        super(source, null);
        this.experiment = experiment;
    }
}
