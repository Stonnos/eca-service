package com.ecaservice.ers.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Evaluation results response model.
 *
 * @author Roman Batygin
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Evaluation results response model")
public class EvaluationResultsResponse {

    /**
     * Request id
     */
    @Schema(description = "Request id")
    private String requestId;
}
