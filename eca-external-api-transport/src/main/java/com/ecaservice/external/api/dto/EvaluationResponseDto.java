package com.ecaservice.external.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.io.Serializable;
import java.math.BigDecimal;

import static com.ecaservice.external.api.dto.Constraints.MAX_INTEGER_VALUE_STRING;
import static com.ecaservice.external.api.dto.Constraints.MAX_LENGTH_255;
import static com.ecaservice.external.api.dto.Constraints.UUID_MAX_LENGTH;
import static com.ecaservice.external.api.dto.Constraints.VALUE_0_STRING;
import static com.ecaservice.external.api.dto.Constraints.VALUE_100_STRING;
import static com.ecaservice.external.api.dto.Constraints.VALUE_1_STRING;
import static com.ecaservice.external.api.dto.Constraints.VALUE_2_STRING;

/**
 * Evaluation response dto.
 *
 * @author Roman Batygin
 */
@Data
@Builder
@Schema(description = "Evaluation response model")
public class EvaluationResponseDto implements Serializable {

    @Tolerate
    public EvaluationResponseDto() {
        //default constructor
    }

    /**
     * Evaluation request id
     */
    @Schema(description = "Evaluation request id", example = "1cbe6c49-8432-4c81-9afa-90f04a803fed",
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

    /**
     * Classifier model url
     */
    @Schema(description = "Model url",
            example = "http://localhost:8080/external-api/download-model/1cbe6c49-8432-4c81-9afa-90f04a803fed",
            maxLength = MAX_LENGTH_255)
    private String modelUrl;

    /**
     * Test instances number
     */
    @Schema(description = "Test instances number", example = "150", minimum = VALUE_2_STRING,
            maximum = MAX_INTEGER_VALUE_STRING)
    private Integer numTestInstances;

    /**
     * Correctly classified instances number
     */
    @Schema(description = "Correctly classified instances number", example = "144", minimum = VALUE_0_STRING,
            maximum = MAX_INTEGER_VALUE_STRING)
    private Integer numCorrect;

    /**
     * Incorrectly classified instances number
     */
    @Schema(description = "Incorrectly classified instances number", example = "6", minimum = VALUE_0_STRING,
            maximum = MAX_INTEGER_VALUE_STRING)
    private Integer numIncorrect;

    /**
     * Correctly classified percentage
     */
    @Schema(description = "Correctly classified percentage", example = "96", minimum = VALUE_0_STRING,
            maximum = VALUE_100_STRING)
    private BigDecimal pctCorrect;

    /**
     * Incorrectly classified percentage
     */
    @Schema(description = "Incorrectly classified percentage", example = "4", minimum = VALUE_0_STRING,
            maximum = VALUE_100_STRING)
    private BigDecimal pctIncorrect;

    /**
     * Mean absolute error
     */
    @Schema(description = "Mean absolute error", example = "0.029", minimum = VALUE_0_STRING,
            maximum = VALUE_1_STRING)
    private BigDecimal meanAbsoluteError;
}
