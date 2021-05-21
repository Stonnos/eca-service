package com.ecaservice.event.model;

import com.ecaservice.model.entity.Experiment;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Experiment change status event.
 *
 * @author Roman Batygin
 */
public class ExperimentNotificationEvent extends ApplicationEvent {

    /**
     * Experiment entity
     */
    @Getter
    private final Experiment experiment;

    /**
     * Notify web push?
     */
    @Getter
    private boolean notifyWebPush = true;

    /**
     * Create a new ExperimentChangeStatusEvent.
     *
     * @param source     - the object on which the event initially occurred (never {@code null})
     * @param experiment - experiment entity
     */
    public ExperimentNotificationEvent(Object source, Experiment experiment) {
        super(source);
        this.experiment = experiment;
    }

    /**
     * Create a new ExperimentChangeStatusEvent.
     *
     * @param source        - the object on which the event initially occurred (never {@code null})
     * @param experiment    - experiment entity
     * @param notifyWebPush - notify web push?
     */
    public ExperimentNotificationEvent(Object source, Experiment experiment, boolean notifyWebPush) {
        this(source, experiment);
        this.notifyWebPush = notifyWebPush;
    }
}
