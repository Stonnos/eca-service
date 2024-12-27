package com.ecaservice.server.model.experiment;

import com.ecaservice.base.model.ExperimentType;
import com.ecaservice.server.model.AbstractEvaluationRequestData;
import eca.core.evaluation.EvaluationMethod;
import lombok.Getter;
import lombok.Setter;

/**
 * Experiment request data model.
 *
 * @author Roman Batygin
 */
@Getter
@Setter
public class ExperimentRequestData extends AbstractEvaluationRequestData {

    /**
     * Email
     */
    private String email;

    /**
     * Experiment type
     */
    private ExperimentType experimentType;

    /**
     * Evaluation method
     */
    private EvaluationMethod evaluationMethod;
}
