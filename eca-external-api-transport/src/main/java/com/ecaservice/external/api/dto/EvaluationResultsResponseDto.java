package com.ecaservice.external.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.experimental.Tolerate;

import java.math.BigDecimal;

import static com.ecaservice.external.api.dto.Constraints.MAX_INTEGER_VALUE_STRING;
import static com.ecaservice.external.api.dto.Constraints.MODEL_URL_MAX_LENGTH;
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
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
@Schema(description = "Evaluation results response model")
public class EvaluationResultsResponseDto extends SimpleEvaluationResponseDto {

    @Tolerate
    public EvaluationResultsResponseDto() {
        //default constructor
    }

    /**
     * Classifier model url
     */
    @Schema(description = "Model url", maxLength = MODEL_URL_MAX_LENGTH)
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
