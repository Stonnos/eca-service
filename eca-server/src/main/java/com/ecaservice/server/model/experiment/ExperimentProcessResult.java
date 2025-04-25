package com.ecaservice.server.model.experiment;

import com.ecaservice.server.model.EvaluationStatus;
import eca.dataminer.AbstractExperiment;
import lombok.Data;

/**
 * Experiment process result model.
 *
 * @author Roman Batygin
 */
@Data
public class ExperimentProcessResult {

    /**
     * Evaluation status
     */
    private EvaluationStatus evaluationStatus;

    /**
     * Experiment history
     */
    private AbstractExperiment<?> experimentHistory;
}
