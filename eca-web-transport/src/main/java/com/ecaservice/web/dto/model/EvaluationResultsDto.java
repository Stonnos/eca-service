package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * Evaluation results dto model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Classifier evaluation results model")
public class EvaluationResultsDto {

    /**
     * Evaluation log results status
     */
    @Schema(description = "Evaluation results status")
    private EnumDto evaluationResultsStatus;

    /**
     * Evaluation results dto (main factors)
     */
    @Schema(description = "Evaluation statistics report (main factors)")
    private EvaluationStatisticsDto evaluationStatisticsDto;

    /**
     * Classification costs results
     */
    @Schema(description = "Classification costs results")
    private List<ClassificationCostsDto> classificationCosts;
}
