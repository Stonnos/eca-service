package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;

import static com.ecaservice.web.dto.util.FieldConstraints.MAX_INTEGER_VALUE_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_255;
import static com.ecaservice.web.dto.util.FieldConstraints.MIN_INTEGER_VALUE_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.ZERO_VALUE_STRING;

/**
 * Frequency diagram data dto.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Frequency diagram data dto")
public class FrequencyDiagramDataDto {

    /**
     * Attribute value code
     */
    @Schema(description = "Attribute value code", example = "value1", maxLength = MAX_LENGTH_255)
    private String code;

    /**
     * Frequency value
     */
    @Schema(description = "Frequency value", example = "0", minimum = ZERO_VALUE_STRING, maximum =
            MAX_INTEGER_VALUE_STRING)
    private int frequency;

    /**
     * Interval lower bound
     */
    @DecimalMin(MIN_INTEGER_VALUE_STRING)
    @DecimalMax(MAX_INTEGER_VALUE_STRING)
    @Schema(description = "Interval lower bound", example = "0.0")
    private BigDecimal lowerBound;

    /**
     * Interval upper bound
     */
    @DecimalMin(MIN_INTEGER_VALUE_STRING)
    @DecimalMax(MAX_INTEGER_VALUE_STRING)
    @Schema(description = "Interval upper bound", example = "0.0")
    private BigDecimal upperBound;
}
