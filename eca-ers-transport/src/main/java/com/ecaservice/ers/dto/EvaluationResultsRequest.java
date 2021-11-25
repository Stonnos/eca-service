package com.ecaservice.ers.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

import static com.ecaservice.ers.dto.Constraints.MIN_1;
import static com.ecaservice.ers.dto.Constraints.UUID_MAX_SIZE;
import static com.ecaservice.ers.dto.Constraints.UUID_PATTERN;

/**
 * Evaluation results request model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Evaluation results request model")
public class EvaluationResultsRequest {

    /**
     * Request id
     */
    @NotBlank
    @Pattern(regexp = UUID_PATTERN)
    @Size(min = MIN_1, max = UUID_MAX_SIZE)
    @Schema(description = "Request id", example = "1d2de514-3a87-4620-9b97-c260e24340de", required = true)
    private String requestId;

    /**
     * Instances report
     */
    @NotNull
    @Valid
    @Schema(description = "Instances report", required = true)
    private InstancesReport instances;

    /**
     * Classifier report
     */
    @NotNull
    @Valid
    @Schema(description = "Classifier report", required = true)
    private ClassifierReport classifierReport;

    /**
     * Evaluation method report
     */
    @NotNull
    @Valid
    @Schema(description = "Evaluation method report", required = true)
    private EvaluationMethodReport evaluationMethodReport;

    /**
     * Statistics report
     */
    @NotNull
    @Valid
    @Schema(description = "Statistics report", required = true)
    private StatisticsReport statistics;

    /**
     * Classification costs report
     */
    @Valid
    @Schema(description = "Classification costs report")
    private List<ClassificationCostsReport> classificationCosts;

    /**
     * Confusion matrix report
     */
    @Valid
    @Schema(description = "Confusion matrix report")
    private List<ConfusionMatrixReport> confusionMatrix;
}
