package com.ecaservice.dto;

import com.ecaservice.dto.json.InstancesDeserializer;
import com.ecaservice.model.experiment.ExperimentType;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import eca.core.evaluation.EvaluationMethod;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import weka.core.Instances;

/**
 * Experiment request transport model.
 *
 * @author Roman Batygin
 */
@Data
@ApiModel(description = "Experiment request model")
public class ExperimentRequest {

    /**
     * First name
     */
    @ApiModelProperty(value = "Clients first name", required = true)
    private String firstName;

    /**
     * Email
     */
    @ApiModelProperty(value = "Clients email", required = true)
    private String email;

    /**
     * Experiment type
     */
    @ApiModelProperty(value = "Experiment algorithm type", required = true)
    private ExperimentType experimentType;

    /**
     * Training data
     */
    @ApiModelProperty(value = "Training data", required = true)
    @JsonDeserialize(using = InstancesDeserializer.class)
    private Instances data;

    /**
     * Evaluation method
     */
    @ApiModelProperty(value = "Evaluation method", required = true)
    private EvaluationMethod evaluationMethod;

}
