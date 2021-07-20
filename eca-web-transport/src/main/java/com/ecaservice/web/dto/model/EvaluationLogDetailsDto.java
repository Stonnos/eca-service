package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Evaluation log details dto model.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Classifier evaluation log details model")
public class EvaluationLogDetailsDto extends EvaluationLogDto {

    /**
     * Evaluation results dto (main factors)
     */
    @Schema(description = "Evaluation results report (main factors)")
    private EvaluationResultsDto evaluationResultsDto;
}
