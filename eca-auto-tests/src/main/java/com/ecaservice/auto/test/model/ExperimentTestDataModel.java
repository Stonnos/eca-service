package com.ecaservice.auto.test.model;

import com.ecaservice.base.model.ExperimentType;
import eca.core.evaluation.EvaluationMethod;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Experiment test data model.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ExperimentTestDataModel extends AbstractEvaluationTestDataModel {

    /**
     * Experiment type
     */
    private ExperimentType experimentType;

    /**
     * Evaluation method
     */
    private EvaluationMethod evaluationMethod;
}
