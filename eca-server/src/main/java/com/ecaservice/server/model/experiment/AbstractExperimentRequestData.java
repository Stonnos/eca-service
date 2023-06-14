package com.ecaservice.server.model.experiment;

import com.ecaservice.base.model.ExperimentType;
import eca.core.evaluation.EvaluationMethod;
import lombok.Data;
import weka.core.Instances;

/**
 * Abstract experiment request data model.
 *
 * @author Roman Batygin
 */
@Data
public abstract class AbstractExperimentRequestData {

    /**
     * Email
     */
    private String email;

    /**
     * Experiment type
     */
    private ExperimentType experimentType;

    /**
     * Training data
     */
    private Instances data;

    /**
     * Evaluation method
     */
    private EvaluationMethod evaluationMethod;

    /**
     * Applies visitor.
     *
     * @param visitor - visitor interface
     */
    public abstract void visit(ExperimentRequestDataVisitor visitor);
}
