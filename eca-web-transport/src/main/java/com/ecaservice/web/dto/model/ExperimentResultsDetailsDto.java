package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Experiment results details dto model.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Experiment results details model")
public class ExperimentResultsDetailsDto extends ExperimentResultsDto {

    /**
     * Experiment dto
     */
    @Schema(description = "Experiment model")
    private ExperimentDto experimentDto;

    /**
     * Evaluation results dto (main factors)
     */
    @Schema(description = "Evaluation results report (main factors)")
    private EvaluationResultsDto evaluationResultsDto;
}
