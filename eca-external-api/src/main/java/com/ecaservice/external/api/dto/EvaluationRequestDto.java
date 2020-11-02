package com.ecaservice.external.api.dto;

import com.ecaservice.classifier.options.model.ClassifierOptions;
import eca.core.evaluation.EvaluationMethod;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import static com.ecaservice.external.api.dto.Constraints.MIN_FOLDS;
import static com.ecaservice.external.api.dto.Constraints.MIN_TESTS;

/**
 * Evaluation request dto model.
 *
 * @author Roman Batygin
 */
@Data
public class EvaluationRequestDto {

    /**
     * Training data url
     */
    @NotEmpty
    private String trainDataUrl;

    /**
     * Classifier input options json config
     */
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
    @Min(MIN_FOLDS)
    private Integer numFolds;

    /**
     * Tests number for k * V cross - validation method
     */
    @Min(MIN_TESTS)
    private Integer numTests;

    /**
     * Seed value for k * V cross - validation method
     */
    private Integer seed;
}
