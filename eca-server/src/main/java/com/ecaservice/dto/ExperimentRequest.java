package com.ecaservice.dto;

import com.ecaservice.dto.json.InstancesDeserializer;
import com.ecaservice.model.experiment.ExperimentType;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import eca.core.evaluation.EvaluationMethod;
import lombok.Data;
import weka.core.Instances;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static com.ecaservice.util.FieldConstraints.EMAIL_MAX_SIZE;
import static com.ecaservice.util.FieldConstraints.EMAIL_REGEX;
import static com.ecaservice.util.FieldConstraints.FIRST_NAME_MAX_SIZE;

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
    @NotBlank
    @Size(max = FIRST_NAME_MAX_SIZE)
    private String firstName;

    /**
     * Email
     */
    @Email(regexp = EMAIL_REGEX)
    @Size(max = EMAIL_MAX_SIZE)
    private String email;

    /**
     * Experiment type
     */
    @NotNull
    private ExperimentType experimentType;

    /**
     * Training data
     */
    @NotNull
    @JsonDeserialize(using = InstancesDeserializer.class)
    private Instances data;

    /**
     * Evaluation method
     */
    @NotNull
    private EvaluationMethod evaluationMethod;

}
