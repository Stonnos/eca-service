package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

import static com.ecaservice.web.dto.util.FieldConstraints.VALUE_100_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.VALUE_1_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.ZERO_VALUE_STRING;

/**
 * Roc curve point dto.
 *
 * @author Roman Batygin
 */
@Data
public class RocCurvePointDto {

    /**
     * Specificity value
     */
    @Schema(description = "Specificity value", example = "65", minimum = ZERO_VALUE_STRING, maximum = VALUE_100_STRING)
    private BigDecimal specificity;

    /**
     * Sensitivity value
     */
    @Schema(description = "Sensitivity value", example = "59", minimum = ZERO_VALUE_STRING, maximum = VALUE_100_STRING)
    private BigDecimal sensitivity;

    /**
     * Threshold value
     */
    @Schema(description = "Threshold value", example = "0.9", minimum = ZERO_VALUE_STRING, maximum = VALUE_1_STRING)
    private BigDecimal threshold;
}
