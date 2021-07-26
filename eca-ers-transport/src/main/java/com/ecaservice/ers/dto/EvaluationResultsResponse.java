package com.ecaservice.ers.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Evaluation results response model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Evaluation results response model")
public class EvaluationResultsResponse {

    /**
     * Request id
     */
    @Schema(description = "Request id")
    private String requestId;
    /**
     * Response status
     */
    @Schema(description = "Response status")
    private ResponseStatus status;
}
