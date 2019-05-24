package com.ecaservice.web.dto.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Evaluation log details dto model.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "Classifier evaluation log details model")
public class EvaluationLogDetailsDto extends EvaluationLogDto {

    /**
     * Evaluation log results status
     */
    @ApiModelProperty(value = "Evaluation results status")
    private EvaluationResultsStatus evaluationResultsStatus;

    /**
     * Evaluation results dto (main factors)
     */
    @ApiModelProperty(value = "Evaluation results report (main factors)")
    private EvaluationResultsDto evaluationResultsDto;

    /**
     * Classification costs results
     */
    @ApiModelProperty(value = "Classification costs results")
    private List<ClassificationCostsDto> classificationCosts;

    /**
     * Roc - curves data
     */
    @ApiModelProperty(value = "Roc - curves data")
    private List<RocCurveDataDto> rocCurveData;
}
