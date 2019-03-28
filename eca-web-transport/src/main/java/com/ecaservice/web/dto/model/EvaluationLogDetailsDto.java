package com.ecaservice.web.dto.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
     * Evaluation results dto
     */
    @ApiModelProperty(value = "Evaluation results report")
    private EvaluationResultsDto evaluationResultsDto;
}
