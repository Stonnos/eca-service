package com.ecaservice.web.dto.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Evaluation results dto model.
 *
 * @author Roman Batygin
 */
@Data
@ApiModel(description = "Classifier evaluation results model")
public class EvaluationResultsDto {

    /**
     * Evaluation log results status
     */
    @ApiModelProperty(value = "Evaluation results status")
    private EnumDto evaluationResultsStatus;

    /**
     * Evaluation results dto (main factors)
     */
    @ApiModelProperty(value = "Evaluation statistics report (main factors)")
    private EvaluationStatisticsDto evaluationStatisticsDto;

    /**
     * Classification costs results
     */
    @ApiModelProperty(value = "Classification costs results")
    private List<ClassificationCostsDto> classificationCosts;
}
