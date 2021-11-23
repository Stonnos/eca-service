package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_255;
import static com.ecaservice.web.dto.util.FieldConstraints.VALUE_1_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.ZERO_VALUE_STRING;

/**
 * Classification costs dto.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Classification costs model")
public class ClassificationCostsDto {

    /**
     * Class value
     */
    @Schema(description = "Class value", example = "Iris-setosa", maxLength = MAX_LENGTH_255)
    private String classValue;

    /**
     * TP rate
     */
    @Schema(description = "TP rate", example = "0.75", minimum = ZERO_VALUE_STRING, maximum = VALUE_1_STRING)
    private BigDecimal truePositiveRate;

    /**
     * FP rate
     */
    @Schema(description = "FP rate", example = "0.25", minimum = ZERO_VALUE_STRING, maximum = VALUE_1_STRING)
    private BigDecimal falsePositiveRate;

    /**
     * TN rate
     */
    @Schema(description = "TN rate", example = "0.5", minimum = ZERO_VALUE_STRING, maximum = VALUE_1_STRING)
    private BigDecimal trueNegativeRate;

    /**
     * FN rate
     */
    @Schema(description = "FN rate", example = "0.5", minimum = "0", maximum = "1")
    private BigDecimal falseNegativeRate;

    /**
     * AUC value
     */
    @Schema(description = "AUC value", example = "0.9", minimum = ZERO_VALUE_STRING, maximum = VALUE_1_STRING)
    private BigDecimal aucValue;
}
