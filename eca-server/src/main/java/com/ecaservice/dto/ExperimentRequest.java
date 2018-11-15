package com.ecaservice.dto;

import com.ecaservice.dto.json.InstancesDeserializer;
import com.ecaservice.model.experiment.ExperimentType;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import eca.core.evaluation.EvaluationMethod;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import weka.core.Instances;

/**
 * Experiment request transport model.
 *
 * @author Roman Batygin
 */
@Data
public class ExperimentRequest {

    /**
     * First name
     */
    @ApiModelProperty(notes = "Clients first name", required = true)
    private String firstName;

    /**
     * Email
     */
    @ApiModelProperty(notes = "Clients email", required = true)
    private String email;

    /**
     * Experiment type
     */
    @ApiModelProperty(notes = "Experiment algorithm type", required = true)
    private ExperimentType experimentType;

    /**
     * Training data
     */
    @ApiModelProperty(notes = "Training data", required = true)
    @JsonDeserialize(using = InstancesDeserializer.class)
    private Instances data;

    /**
     * Evaluation method
     */
    @ApiModelProperty(notes = "Evaluation method", required = true)
    private EvaluationMethod evaluationMethod;

}
