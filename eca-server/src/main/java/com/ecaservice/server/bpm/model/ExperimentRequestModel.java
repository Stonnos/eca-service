package com.ecaservice.server.bpm.model;

import com.ecaservice.base.model.ExperimentType;
import eca.core.evaluation.EvaluationMethod;
import lombok.Getter;
import lombok.Setter;

/**
 * Abstract experiment request model for bpmn process.
 *
 * @author Roman Batygin
 */
@Getter
@Setter
public class ExperimentRequestModel extends AbstractEvaluationRequestModel {

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
     * Reply to queue
     */
    private String replyTo;

    /**
     * MQ message correlation id
     */
    private String correlationId;
}
