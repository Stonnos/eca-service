package com.ecaservice.server.event.model.push;

import com.ecaservice.core.push.client.event.model.AbstractSystemPushEvent;
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
    @Getter
    private final PushMessageParams pushMessageParams;

    /**
     * Create a new ExperimentSystemPushEvent.
     *
     * @param source            - the object on which the event initially occurred (never {@code null})
     * @param experiment        - experiment entity
     * @param pushMessageParams - push message params
     */
    public ExperimentSystemPushEvent(Object source, Experiment experiment, PushMessageParams pushMessageParams) {
        super(source);
        this.experiment = experiment;
        this.pushMessageParams = pushMessageParams;
    }
}
