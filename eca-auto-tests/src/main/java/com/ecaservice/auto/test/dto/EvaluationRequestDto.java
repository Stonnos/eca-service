package com.ecaservice.auto.test.dto;

import com.ecaservice.auto.test.model.evaluation.EvaluationResultsDetailsMatch;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Evaluation request dto.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EvaluationRequestDto extends BaseEvaluationRequestDto {

    /**
     * Classifier name
     */
    @Schema(description = "Classifier name")
    private String classifierName;

    /**
     * Classifier options json config
     */
    @Schema(description = "Classifier options json config")
    private String classifierOptions;

    /**
     * Folds number for k * V cross - validation method
     */
    @Schema(description = "Folds number for k * V cross - validation method")
    private Integer numFolds;

    /**
     * Tests number for k * V cross - validation method
     */
    @Schema(description = "Tests number for k * V cross - validation method")
    private Integer numTests;

    /**
     * Seed value for random generator
     */
    @Schema(description = "Seed value for random generator")
    private Integer seed;

    /**
     * Evaluation results details match
     */
    @Schema(description = "Evaluation results details match")
    private EvaluationResultsDetailsMatch evaluationResultsDetails;
}
