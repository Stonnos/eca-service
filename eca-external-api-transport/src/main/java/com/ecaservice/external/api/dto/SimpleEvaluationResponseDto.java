package com.ecaservice.external.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.experimental.Tolerate;

import java.io.Serializable;

import static com.ecaservice.external.api.dto.Constraints.MAX_LENGTH_255;
import static com.ecaservice.external.api.dto.Constraints.UUID_MAX_LENGTH;

/**
 * Simple response dto.
 *
 * @author Roman Batygin
 */
@Data
@SuperBuilder
@Schema(description = "Simple response model")
public class SimpleEvaluationResponseDto implements Serializable {

    @Tolerate
    public SimpleEvaluationResponseDto() {
        //default constructor
    }

    /**
     * Experiment request id
     */
    @Schema(description = "Experiment request id", example = "1cbe6c49-8432-4c81-9afa-90f04a803fed",
            maxLength = UUID_MAX_LENGTH)
    private String requestId;

    /**
     * Evaluation status
     */
    @Schema(description = "Evaluation status", example = "FINISHED", maxLength = MAX_LENGTH_255)
    private EvaluationStatus evaluationStatus;

    /**
     * Error code
     */
    @Schema(description = "Error code", example = "INTERNAL_SERVER_ERROR", maxLength = MAX_LENGTH_255)
    private String errorCode;
}
