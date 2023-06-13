package com.ecaservice.ers.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

import static com.ecaservice.ers.dto.Constraints.MAX_LENGTH_255;

/**
 * Evaluation results statistics sort field model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Evaluation results statistics sort field model")
public class EvaluationResultsStatisticsSortField {

    /**
     * Sort field name
     */
    @NotNull
    @Schema(description = "Sort field name", required = true)
    private EvaluationResultsStatisticsField field;

    /**
     * Sort direction
     */
    @NotNull
    @Schema(description = "Sort direction", maxLength = MAX_LENGTH_255)
    private SortDirection direction;
}
