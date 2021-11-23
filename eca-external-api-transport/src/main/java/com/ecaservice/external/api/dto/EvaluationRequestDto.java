package com.ecaservice.external.api.dto;

import com.ecaservice.classifier.options.model.ClassifierOptions;
import com.ecaservice.external.api.dto.annotations.DataURL;
import eca.core.evaluation.EvaluationMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

import static com.ecaservice.external.api.dto.Constraints.MAX_FOLDS;
import static com.ecaservice.external.api.dto.Constraints.MAX_LENGTH_255;
import static com.ecaservice.external.api.dto.Constraints.MAX_TESTS;
import static com.ecaservice.external.api.dto.Constraints.MIN_FOLDS;
import static com.ecaservice.external.api.dto.Constraints.MIN_LENGTH_1;
import static com.ecaservice.external.api.dto.Constraints.MIN_TESTS;

/**
 * Evaluation request dto model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Evaluation request model")
public class EvaluationRequestDto implements Serializable {

    /**
     * Training data url
     */
    @DataURL
    @Size(min = MIN_LENGTH_1, max = MAX_LENGTH_255)
    @Schema(description = "Train data url", example = "http://kt.ijs.si/Branax/Repository/WEKA/Iris.xls",
            required = true)
    private String trainDataUrl;

    /**
     * Classifier input options json config
     */
    @Valid
    @NotNull
    @Schema(description = "Classifier options json", required = true)
    private ClassifierOptions classifierOptions;

    /**
     * Evaluation method
     */
    @NotNull
    @Schema(description = "Evaluation method", required = true, maxLength = MAX_LENGTH_255)
    private EvaluationMethod evaluationMethod;

    /**
     * Folds number for k * V cross - validation method
     */
    @Min(MIN_FOLDS)
    @Max(MAX_FOLDS)
    @Schema(description = "Folds number for k * V cross - validation method", example = "10")
    private Integer numFolds;

    /**
     * Tests number for k * V cross - validation method
     */
    @Min(MIN_TESTS)
    @Max(MAX_TESTS)
    @Schema(description = "Tests number for k * V cross - validation method", example = "1")
    private Integer numTests;

    /**
     * Seed value for k * V cross - validation method
     */
    @Min(Integer.MIN_VALUE)
    @Max(Integer.MAX_VALUE)
    @Schema(description = "Seed value for k * V cross - validation method", example = "1")
    private Integer seed;
}
