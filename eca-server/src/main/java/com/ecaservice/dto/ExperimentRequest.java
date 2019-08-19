package com.ecaservice.dto;

import com.ecaservice.dto.json.InstancesDeserializer;
import com.ecaservice.model.experiment.ExperimentType;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import eca.core.evaluation.EvaluationMethod;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import weka.core.Instances;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static com.ecaservice.util.FieldConstraints.EMAIL_REGEX;
import static com.ecaservice.util.FieldConstraints.EMAIL_MAX_SIZE;
import static com.ecaservice.util.FieldConstraints.FIRST_NAME_MAX_SIZE;

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
    @NotBlank
    @Size(max = FIRST_NAME_MAX_SIZE)
    @ApiModelProperty(value = "Clients first name", required = true)
    private String firstName;

    /**
     * Email
     */
    @Email(regexp = EMAIL_REGEX)
    @Size(max = EMAIL_MAX_SIZE)
    @ApiModelProperty(value = "Clients email", required = true)
    private String email;

    /**
     * Experiment type
     */
    @NotNull
    @ApiModelProperty(value = "Experiment algorithm type", required = true)
    private ExperimentType experimentType;

    /**
     * Training data
     */
    @NotNull
    @ApiModelProperty(value = "Training data", required = true)
    @JsonDeserialize(using = InstancesDeserializer.class)
    private Instances data;

    /**
     * Evaluation method
     */
    @NotNull
    @ApiModelProperty(value = "Evaluation method", required = true)
    private EvaluationMethod evaluationMethod;

}
