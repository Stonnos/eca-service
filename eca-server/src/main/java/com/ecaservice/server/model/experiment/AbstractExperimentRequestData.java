package com.ecaservice.server.model.experiment;

import com.ecaservice.base.model.ExperimentType;
import eca.core.evaluation.EvaluationMethod;
import lombok.Data;

import java.io.Serializable;

/**
 * Abstract experiment request data model.
 *
 * @author Roman Batygin
 */
@Data
public abstract class AbstractExperimentRequestData implements Serializable {

    /**
     * Request id
     */
    private String requestId;

    /**
     * Train data uuid
     */
    private String dataUuid;

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

    /**
     * Applies visitor.
     *
     * @param visitor - visitor interface
     */
    public abstract void visit(ExperimentRequestDataVisitor visitor);
}
