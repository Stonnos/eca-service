package com.ecaservice.auto.test.event.model;

import com.ecaservice.auto.test.entity.autotest.EvaluationResultsTestStepEntity;
import eca.core.evaluation.EvaluationResults;
import lombok.Getter;

/**
 * Evaluation results test step event.
 *
 * @author Roman Batygin
 */
@Getter
public class EvaluationResultsTestStepEvent extends AbstractTestStepEvent {

    private final EvaluationResultsTestStepEntity evaluationResultsTestStepEntity;
    private final EvaluationResults evaluationResults;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source                          the object on which the event initially occurred or with
     *                                        which the event is associated (never {@code null})
     * @param evaluationResultsTestStepEntity - evaluation results test step entity
     * @param evaluationResults               - evaluation results
     */
    public EvaluationResultsTestStepEvent(Object source,
                                          EvaluationResultsTestStepEntity evaluationResultsTestStepEntity,
                                          EvaluationResults evaluationResults) {
        super(source);
        this.evaluationResultsTestStepEntity = evaluationResultsTestStepEntity;
        this.evaluationResults = evaluationResults;
    }
}
