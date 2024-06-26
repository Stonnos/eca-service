package com.ecaservice.base.model;

import com.ecaservice.base.model.visitor.EcaRequestVisitor;
import com.ecaservice.classifier.options.model.ClassifierOptions;
import eca.core.evaluation.EvaluationMethod;
import lombok.Data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Evaluation request transport model.
 *
 * @author Roman Batygin
 */
@Data
public class EvaluationRequest implements EcaRequest {

    /**
     * Train data uuid
     */
    @NotBlank
    private String dataUuid;

    /**
     * Classifier options
     */
    @Valid
    @NotNull
    private ClassifierOptions classifierOptions;

    /**
     * Evaluation method
     */
    @NotNull
    private EvaluationMethod evaluationMethod;

    /**
     * Folds number for k * V cross - validation method
     */
    private Integer numFolds;

    /**
     * Tests number for k * V cross - validation method
     */
    private Integer numTests;

    /**
     * Seed value for k * V cross - validation method
     */
    private Integer seed;

    @Override
    public void visit(EcaRequestVisitor ecaRequestVisitor) {
        ecaRequestVisitor.visitEvaluationRequest(this);
    }
}
