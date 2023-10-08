package com.ecaservice.server.event.model;

import com.ecaservice.server.bpm.model.EvaluationResultsModel;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Evaluation response event.
 *
 * @author Roman Batygin
 */
public class EvaluationResponseEvent extends ApplicationEvent {

    /**
     * Experiment entity
     */
    @Getter
    private final EvaluationResultsModel evaluationResultsModel;

    /**
     * Correlation id
     */
    @Getter
    private final String correlationId;

    /**
     * Reply to queue
     */
    @Getter
    private final String replyTo;

    /**
     * Create a new event.
     *
     * @param source                 - the object on which the event initially occurred (never {@code null})
     * @param evaluationResultsModel - evaluation results model
     * @param correlationId          - correlation id
     * @param replyTo                - reply to queue
     */
    public EvaluationResponseEvent(Object source,
                                   EvaluationResultsModel evaluationResultsModel,
                                   String correlationId,
                                   String replyTo) {
        super(source);
        this.evaluationResultsModel = evaluationResultsModel;
        this.correlationId = correlationId;
        this.replyTo = replyTo;
    }
}
