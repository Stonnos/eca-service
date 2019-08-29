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
     * Evaluation results dto (main factors)
     */
    @ApiModelProperty(value = "Evaluation results report (main factors)")
    private EvaluationResultsDto evaluationResultsDto;
}
