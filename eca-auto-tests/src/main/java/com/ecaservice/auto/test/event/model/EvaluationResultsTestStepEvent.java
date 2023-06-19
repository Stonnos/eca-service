package com.ecaservice.auto.test.event.model;

import com.ecaservice.auto.test.entity.autotest.EvaluationResultsTestStepEntity;
import lombok.Getter;

/**
 * Evaluation results test step event.
 *
 * @author Roman Batygin
 */
@Getter
public class EvaluationResultsTestStepEvent extends AbstractTestStepEvent {

    private final EvaluationResultsTestStepEntity evaluationResultsTestStepEntity;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source                          the object on which the event initially occurred or with
     *                                        which the event is associated (never {@code null})
     * @param evaluationResultsTestStepEntity - evaluation results test step entity
     */
    public EvaluationResultsTestStepEvent(Object source,
                                          EvaluationResultsTestStepEntity evaluationResultsTestStepEntity) {
        super(source);
        this.evaluationResultsTestStepEntity = evaluationResultsTestStepEntity;
    }
}
