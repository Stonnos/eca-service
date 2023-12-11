package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Classifier evaluation results history dto.
 *
 * @author Roman Batygin
 */
@Schema(description = "Classifier evaluation results history page dto")
public class EvaluationResultsHistoryPageDto extends PageDto<EvaluationResultsHistoryDto> {
}
