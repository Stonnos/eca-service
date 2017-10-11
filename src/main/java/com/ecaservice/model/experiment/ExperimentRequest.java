package com.ecaservice.model.experiment;

import com.ecaservice.model.evaluation.EvaluationMethod;
import lombok.Data;
import weka.core.Instances;

/**
 * @author Roman Batygin
 */
@Data
public class ExperimentRequest {

    private String firstName;

    private String email;

    private String ipAddress;

    private ExperimentType experimentType;

    private Instances data;

    private EvaluationMethod evaluationMethod;

}
