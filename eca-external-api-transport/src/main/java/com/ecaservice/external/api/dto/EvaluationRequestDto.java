package com.ecaservice.external.api.dto;

import com.ecaservice.classifier.options.model.ClassifierOptions;
import com.ecaservice.external.api.dto.annotations.DataURL;
import eca.core.evaluation.EvaluationMethod;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import java.io.Serializable;

import static com.ecaservice.external.api.dto.Constraints.MAX_FOLDS;
import static com.ecaservice.external.api.dto.Constraints.MAX_TESTS;
import static com.ecaservice.external.api.dto.Constraints.MIN_FOLDS;
import static com.ecaservice.external.api.dto.Constraints.MIN_TESTS;

/**
 * Evaluation request dto model.
 *
 * @author Roman Batygin
 */
@Data
public class EvaluationRequestDto implements Serializable {

    /**
     * Training data url
     */
    @DataURL
    @ApiModelProperty(value = "Train data url", example = "http://kt.ijs.si/Branax/Repository/WEKA/Iris.xls",
            required = true)
    private String trainDataUrl;

    /**
     * Classifier input options json config
     */
    @NotNull
    @ApiModelProperty(value = "Classifier options json", required = true)
    private ClassifierOptions classifierOptions;

    /**
     * Evaluation method
     */
    @NotNull
    @ApiModelProperty(value = "Evaluation method", required = true)
    private EvaluationMethod evaluationMethod;

    /**
     * Folds number for k * V cross - validation method
     */
    @Min(MIN_FOLDS)
    @Max(MAX_FOLDS)
    @ApiModelProperty(value = "Folds number for k * V cross - validation method", example = "10")
    private Integer numFolds;

    /**
     * Tests number for k * V cross - validation method
     */
    @Min(MIN_TESTS)
    @Max(MAX_TESTS)
    @ApiModelProperty(value = "Tests number for k * V cross - validation method", example = "1")
    private Integer numTests;

    /**
     * Seed value for k * V cross - validation method
     */
    @ApiModelProperty(value = "Seed value for k * V cross - validation method", example = "1")
    private Integer seed;
}
