package com.ecaservice.ers.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.ecaservice.ers.dto.Constraints.UUID_MAX_SIZE;

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
    @Schema(description = "Request id", maxLength = UUID_MAX_SIZE)
    private String requestId;
}
