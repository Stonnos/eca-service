package com.ecaservice.external.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

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
    @Schema(description = "Evaluation request id")
    private String requestId;

    /**
     * Evaluation status
     */
    @Schema(description = "Evaluation status")
    private EvaluationStatus evaluationStatus;

    /**
     * Classifier model url
     */
    @Schema(description = "Model url")
    private String modelUrl;

    /**
     * Test instances number
     */
    @Schema(description = "Test instances number")
    private BigInteger numTestInstances;

    /**
     * Correctly classified instances number
     */
    @Schema(description = "Correctly classified instances number")
    private BigInteger numCorrect;

    /**
     * Incorrectly classified instances number
     */
    @Schema(description = "Incorrectly classified instances number")
    private BigInteger numIncorrect;

    /**
     * Correctly classified percentage
     */
    @Schema(description = "Correctly classified percentage")
    private BigDecimal pctCorrect;

    /**
     * Incorrectly classified percentage
     */
    @Schema(description = "Incorrectly classified percentage")
    private BigDecimal pctIncorrect;

    /**
     * Mean absolute error
     */
    @Schema(description = "Mean absolute error")
    private BigDecimal meanAbsoluteError;
}
