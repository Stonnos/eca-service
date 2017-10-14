package com.ecaservice.dto;

import com.ecaservice.json.ExperimentRequestDeserializer;
import com.ecaservice.model.evaluation.EvaluationMethod;
import com.ecaservice.model.experiment.ExperimentType;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import weka.core.Instances;

/**
 * Experiment request transport model.
 *
 * @author Roman Batygin
 */
@Data
@JsonDeserialize(using = ExperimentRequestDeserializer.class)
public class ExperimentRequestDto {

    /**
     * First name
     */
    private String firstName;

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

}
