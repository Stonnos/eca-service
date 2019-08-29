package com.ecaservice.web.dto.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Experiment results details dto model.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "Experiment results details model")
public class ExperimentResultsDetailsDto extends ExperimentResultsDto {

    /**
     * Evaluation results dto (main factors)
     */
    @ApiModelProperty(value = "Evaluation results report (main factors)")
    private EvaluationResultsDto evaluationResultsDto;
}
