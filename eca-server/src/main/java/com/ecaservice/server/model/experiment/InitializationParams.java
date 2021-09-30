package com.ecaservice.server.model.experiment;

import eca.core.evaluation.EvaluationMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import weka.core.Instances;

/**
 * Experiment initialization params model.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InitializationParams {

    private Instances data;

    private EvaluationMethod evaluationMethod;
}
