package com.ecaservice.dto;

import com.ecaservice.model.EvaluationMethod;
import com.ecaservice.model.experiment.ExperimentType;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import weka.core.Instances;

/**
 * Experiment request model.
 *
 * @author Roman Batygin
 */
@Data
@JsonDeserialize(using = ExperimentRequestDeserializer.class)
public class ExperimentRequestDto {

    private String firstName;

    private String email;

    private ExperimentType experimentType;

    private Instances data;

    private EvaluationMethod evaluationMethod;

}
