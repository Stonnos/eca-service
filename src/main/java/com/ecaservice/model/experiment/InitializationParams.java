package com.ecaservice.model.experiment;

import com.ecaservice.model.evaluation.EvaluationMethod;
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
