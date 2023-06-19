package com.ecaservice.server.event.model;

import com.ecaservice.server.model.evaluation.EvaluationResultsDataModel;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Classifier evaluation ERS report event.
 *
 * @author Roman Batygin
 */
public class EvaluationErsReportEvent extends ApplicationEvent {

    /**
     * Evaluation response
     */
    @Getter
    private final EvaluationResultsDataModel evaluationResultsDataModel;

    /**
     * Create a new event.
     *
     * @param source                     - the object on which the event initially occurred (never {@code null})
     * @param evaluationResultsDataModel - evaluation response data model
     */
    public EvaluationErsReportEvent(Object source, EvaluationResultsDataModel evaluationResultsDataModel) {
        super(source);
        this.evaluationResultsDataModel = evaluationResultsDataModel;
    }
}
