package com.ecaservice.ers.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Get evaluation results response model.
 *
 * @author Roman Batygin
 */
@Data
@ApiModel(description = "Get evaluation results response model")
public class GetEvaluationResultsResponse {

    /**
     * Request id
     */
    @ApiModelProperty(value = "Request id")
    private String requestId;

    /**
     * Response status
     */
    @ApiModelProperty(value = "Response status")
    private ResponseStatus status;

    /**
     * Instances report
     */
    @ApiModelProperty(value = "Instances report")
    private InstancesReport instances;

    /**
     * Classifier report
     */
    @ApiModelProperty(value = "Classifier report")
    private ClassifierReport classifierReport;

    /**
     * Evaluation method report
     */
    @ApiModelProperty(value = "Evaluation method report")
    private EvaluationMethodReport evaluationMethodReport;

    /**
     * Statistics report
     */
    @ApiModelProperty(value = "Statistics report")
    private StatisticsReport statistics;

    /**
     * Classification costs report
     */
    @ApiModelProperty(value = "Classification costs report")
    private List<ClassificationCostsReport> classificationCosts;

    /**
     * Confusion matrix report
     */
    @ApiModelProperty(value = "Confusion matrix report")
    private List<ConfusionMatrixReport> confusionMatrix;
}
