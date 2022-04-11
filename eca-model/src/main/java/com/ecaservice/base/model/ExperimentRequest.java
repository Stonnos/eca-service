package com.ecaservice.base.model;

import com.ecaservice.base.model.databind.InstancesDeserializer;
import com.ecaservice.base.model.databind.InstancesSerializer;
import com.ecaservice.base.model.visitor.EcaRequestVisitor;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import eca.core.evaluation.EvaluationMethod;
import lombok.Data;
import weka.core.Instances;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static com.ecaservice.base.model.util.FieldConstraints.EMAIL_MAX_SIZE;
import static com.ecaservice.base.model.util.FieldConstraints.EMAIL_REGEX;
import static com.ecaservice.base.model.util.FieldConstraints.FIRST_NAME_MAX_SIZE;

/**
 * Experiment request transport model.
 *
 * @author Roman Batygin
 */
@Data
public class ExperimentRequest implements EcaRequest {

    /**
     * First name
     */
    @NotBlank
    @Size(max = FIRST_NAME_MAX_SIZE)
    private String firstName;

    /**
     * Email
     */
    @NotBlank
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
    @JsonSerialize(using = InstancesSerializer.class)
    @JsonDeserialize(using = InstancesDeserializer.class)
    private Instances data;

    /**
     * Evaluation method
     */
    @NotNull
    private EvaluationMethod evaluationMethod;

    @Override
    public void visit(EcaRequestVisitor ecaRequestVisitor) {
        ecaRequestVisitor.visitExperimentRequest(this);
    }
}
