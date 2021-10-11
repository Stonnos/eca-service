package com.ecaservice.external.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.io.Serializable;
import java.math.BigDecimal;

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
    @Schema(description = "Evaluation request id", example = "1cbe6c49-8432-4c81-9afa-90f04a803fed")
    private String requestId;

    /**
     * Evaluation status
     */
    @Schema(description = "Evaluation status", example = "FINISHED")
    private EvaluationStatus evaluationStatus;

    /**
     * Classifier model url
     */
    @Schema(description = "Model url",
            example = "http://localhost:8080/external-api/download-model/1cbe6c49-8432-4c81-9afa-90f04a803fed")
    private String modelUrl;

    /**
     * Test instances number
     */
    @Schema(description = "Test instances number", example = "150")
    private Integer numTestInstances;

    /**
     * Correctly classified instances number
     */
    @Schema(description = "Correctly classified instances number", example = "144")
    private Integer numCorrect;

    /**
     * Incorrectly classified instances number
     */
    @Schema(description = "Incorrectly classified instances number", example = "6")
    private Integer numIncorrect;

    /**
     * Correctly classified percentage
     */
    @Schema(description = "Correctly classified percentage", example = "96")
    private BigDecimal pctCorrect;

    /**
     * Incorrectly classified percentage
     */
    @Schema(description = "Incorrectly classified percentage", example = "4")
    private BigDecimal pctIncorrect;

    /**
     * Mean absolute error
     */
    @Schema(description = "Mean absolute error", example = "0.029")
    private BigDecimal meanAbsoluteError;
}
