package com.ecaservice.model.experiment;

import eca.core.evaluation.EvaluationMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import weka.core.Instances;

/**
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InitializationParams {

    private Instances data;

    private EvaluationMethod evaluationMethod;
}
