package com.ecaservice.ers.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

import static com.ecaservice.ers.dto.Constraints.UUID_MAX_SIZE;

/**
 * Get evaluation results response model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Get evaluation results response model")
public class GetEvaluationResultsResponse {

    /**
     * Request id
     */
    @Schema(description = "Request id", maxLength = UUID_MAX_SIZE)
    private String requestId;

    /**
     * Instances report
     */
    @Schema(description = "Instances report")
    private InstancesReport instances;

    /**
     * Classifier report
     */
    @Schema(description = "Classifier report")
    private ClassifierReport classifierReport;

    /**
     * Evaluation method report
     */
    @Schema(description = "Evaluation method report")
    private EvaluationMethodReport evaluationMethodReport;

    /**
     * Statistics report
     */
    @Schema(description = "Statistics report")
    private StatisticsReport statistics;

    /**
     * Classification costs report
     */
    @Schema(description = "Classification costs report")
    private List<ClassificationCostsReport> classificationCosts;

    /**
     * Confusion matrix report
     */
    @Schema(description = "Confusion matrix report")
    private List<ConfusionMatrixReport> confusionMatrix;
}
