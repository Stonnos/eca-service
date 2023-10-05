package com.ecaservice.server.model.experiment;

import com.ecaservice.base.model.ExperimentType;
import com.ecaservice.server.model.AbstractEvaluationRequestData;
import com.ecaservice.server.model.entity.Channel;
import eca.core.evaluation.EvaluationMethod;
import lombok.Getter;
import lombok.Setter;

/**
 * Abstract experiment request data model.
 *
 * @author Roman Batygin
 */
@Getter
@Setter
public abstract class AbstractExperimentRequestData extends AbstractEvaluationRequestData {

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

    protected AbstractExperimentRequestData(Channel channel) {
        super(channel);
    }

    /**
     * Applies visitor.
     *
     * @param visitor - visitor interface
     */
    public abstract void visit(ExperimentRequestDataVisitor visitor);
}
