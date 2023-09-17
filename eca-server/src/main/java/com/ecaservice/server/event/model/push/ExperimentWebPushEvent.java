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
    @Getter
    private final PushMessageParams pushMessageParams;

    /**
     * Create a new ExperimentWebPushEvent.
     *
     * @param source            - the object on which the event initially occurred (never {@code null})
     * @param experiment        - experiment entity
     * @param pushMessageParams - push message params
     */
    public ExperimentWebPushEvent(Object source, Experiment experiment, PushMessageParams pushMessageParams) {
        super(source, null);
        this.experiment = experiment;
        this.pushMessageParams = pushMessageParams;
    }
}
