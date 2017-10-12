package com.ecaservice.model.experiment;

import com.ecaservice.model.evaluation.EvaluationMethod;
import lombok.Data;
import weka.core.Instances;

/**
 * Experiment request model.
 * @author Roman Batygin
 */
@Data
public class ExperimentRequest {

    /**
     * First name
     */
    private String firstName;

    /**
     * Email
     */
    private String email;

    /**
     * Remote ip address
     */
    private String ipAddress;

    /**
     * Experiment type
     */
    private ExperimentType experimentType;

    /**
     * Input data
     */
    private Instances data;

    /**
     * Evaluation method
     */
    private EvaluationMethod evaluationMethod;

}
