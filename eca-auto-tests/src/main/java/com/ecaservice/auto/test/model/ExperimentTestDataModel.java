package com.ecaservice.auto.test.model;

import com.ecaservice.base.model.ExperimentType;
import eca.core.evaluation.EvaluationMethod;
import lombok.Data;

/**
 * Experiment test data model.
 *
 * @author Roman Batygin
 */
@Data
public class ExperimentTestDataModel {

    /**
     * Experiment type
     */
    private ExperimentType experimentType;

    /**
     * First name
     */
    private String firstName;

    /**
     * Train data path in resources directory
     */
    private String trainDataPath;

    /**
     * Evaluation method
     */
    private EvaluationMethod evaluationMethod;
}
