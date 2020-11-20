package com.ecaservice.external.api.dto;

import com.ecaservice.classifier.options.model.ClassifierOptions;
import com.ecaservice.external.api.dto.annotations.DataURL;
import eca.core.evaluation.EvaluationMethod;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
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
    @DataURL
    @ApiModelProperty(value = "Train data url")
    private String trainDataUrl;

    /**
     * Classifier input options json config
     */
    @NotNull
    @ApiModelProperty(value = "Classifier options json")
    private ClassifierOptions classifierOptions;

    /**
     * Evaluation method
     */
    @NotNull
    @ApiModelProperty(value = "Evaluation method")
    private EvaluationMethod evaluationMethod;

    /**
     * Folds number for k * V cross - validation method
     */
    @Min(MIN_FOLDS)
    @ApiModelProperty(value = "Folds number for k * V cross - validation method")
    private Integer numFolds;

    /**
     * Tests number for k * V cross - validation method
     */
    @Min(MIN_TESTS)
    @ApiModelProperty(value = "Tests number for k * V cross - validation method")
    private Integer numTests;

    /**
     * Seed value for k * V cross - validation method
     */
    @ApiModelProperty(value = "Seed value for k * V cross - validation method")
    private Integer seed;
}
