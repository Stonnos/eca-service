package com.ecaservice.event.model;

import com.ecaservice.model.entity.Experiment;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Experiment progress event.
 *
 * @author Roman Batygin
 */
public class ExperimentProgressEvent extends ApplicationEvent {

    /**
     * Experiment entity
     */
    @Getter
    private final Experiment experiment;

    /**
     * Experiment progress bar
     */
    @Getter
    private final Integer progress;

    /**
     * Create a new ExperimentProgressEvent.
     *
     * @param source     - the object on which the event initially occurred (never {@code null})
     * @param experiment - experiment entity
     * @param progress   - experiment progress bar
     */
    public ExperimentProgressEvent(Object source, Experiment experiment, Integer progress) {
        super(source);
        this.experiment = experiment;
        this.progress = progress;
    }
}
