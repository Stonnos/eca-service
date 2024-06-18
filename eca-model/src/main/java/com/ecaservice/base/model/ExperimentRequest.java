package com.ecaservice.base.model;

import com.ecaservice.base.model.visitor.EcaRequestVisitor;
import eca.core.evaluation.EvaluationMethod;
import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import static com.ecaservice.base.model.util.FieldConstraints.EMAIL_MAX_SIZE;
import static com.ecaservice.base.model.util.FieldConstraints.EMAIL_REGEX;

/**
 * Experiment request transport model.
 *
 * @author Roman Batygin
 */
@Data
public class ExperimentRequest implements EcaRequest {

    /**
     * Train data uuid
     */
    @NotBlank
    private String dataUuid;

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
     * Evaluation method
     */
    @NotNull
    private EvaluationMethod evaluationMethod;

    @Override
    public void visit(EcaRequestVisitor ecaRequestVisitor) {
        ecaRequestVisitor.visitExperimentRequest(this);
    }
}
