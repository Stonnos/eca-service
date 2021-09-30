package com.ecaservice.server.event.model;

import com.ecaservice.base.model.EvaluationResponse;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Classifier evaluation finished event.
 *
 * @author Roman Batygin
 */
public class EvaluationFinishedEvent extends ApplicationEvent {

    /**
     * Evaluation response
     */
    @Getter
    private final EvaluationResponse evaluationResponse;

    /**
     * Create a new EvaluationFinishedEvent.
     *
     * @param source             - the object on which the event initially occurred (never {@code null})
     * @param evaluationResponse - evaluation response
     */
    public EvaluationFinishedEvent(Object source, EvaluationResponse evaluationResponse) {
        super(source);
        this.evaluationResponse = evaluationResponse;
    }
}
