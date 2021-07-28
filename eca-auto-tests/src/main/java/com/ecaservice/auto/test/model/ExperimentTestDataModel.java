package com.ecaservice.auto.test.model;

import com.ecaservice.base.model.ExperimentType;
import eca.core.evaluation.EvaluationMethod;
import lombok.Data;
import org.springframework.core.io.Resource;

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
     * Train data path in resources directory
     */
    private Resource trainDataPath;

    /**
     * Evaluation method
     */
    private EvaluationMethod evaluationMethod;
}
