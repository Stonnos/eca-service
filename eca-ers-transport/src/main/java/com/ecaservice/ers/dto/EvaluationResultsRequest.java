package com.ecaservice.ers.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

import static com.ecaservice.ers.dto.Constraints.UUID_MAX_SIZE;
import static com.ecaservice.ers.dto.Constraints.UUID_PATTERN;

/**
 * Evaluation results request model.
 *
 * @author Roman Batygin
 */
@Data
@ApiModel(description = "Evaluation results request model")
public class EvaluationResultsRequest {

    /**
     * Request id
     */
    @NotBlank
    @Pattern(regexp = UUID_PATTERN)
    @Size(max = UUID_MAX_SIZE)
    @ApiModelProperty(value = "Request id", example = "1d2de514-3a87-4620-9b97-c260e24340de")
    private String requestId;

    /**
     * Instances report
     */
    @NotNull
    @Valid
    @ApiModelProperty(value = "Instances report")
    private InstancesReport instances;

    /**
     * Classifier report
     */
    @NotNull
    @Valid
    @ApiModelProperty(value = "Classifier report")
    private ClassifierReport classifierReport;

    /**
     * Evaluation method report
     */
    @NotNull
    @Valid
    @ApiModelProperty(value = "Evaluation method report")
    private EvaluationMethodReport evaluationMethodReport;

    /**
     * Statistics report
     */
    @NotNull
    @Valid
    @ApiModelProperty(value = "Statistics report")
    private StatisticsReport statistics;

    /**
     * Classification costs report
     */
    @Valid
    @ApiModelProperty(value = "Classification costs report", allowEmptyValue = true)
    private List<ClassificationCostsReport> classificationCosts;

    /**
     * Confusion matrix report
     */
    @Valid
    @ApiModelProperty(value = "Confusion matrix report", allowEmptyValue = true)
    private List<ConfusionMatrixReport> confusionMatrix;
}
