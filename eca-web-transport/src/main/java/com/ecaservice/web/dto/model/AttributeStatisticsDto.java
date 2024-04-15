package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.util.List;

import static com.ecaservice.web.dto.util.FieldConstraints.DECIMAL_MAX_ONE;
import static com.ecaservice.web.dto.util.FieldConstraints.DECIMAL_MIN_ZERO;
import static com.ecaservice.web.dto.util.FieldConstraints.MAX_INTEGER_VALUE_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_255;
import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LONG_VALUE_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.MIN_INTEGER_VALUE_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.VALUE_1_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.ZERO_VALUE_STRING;

/**
 * Attribute statistics dto model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Attribute statistics model")
public class AttributeStatisticsDto {

    /**
     * Attribute id
     */
    @Schema(description = "Attribute id", example = "1", minimum = VALUE_1_STRING, maximum = MAX_LONG_VALUE_STRING)
    private Long id;

    /**
     * Attribute name
     */
    @Schema(description = "Attribute name", example = "attr_name", maxLength = MAX_LENGTH_255)
    private String name;

    /**
     * Attribute index
     */
    @Schema(description = "Attribute index", example = "0", minimum = ZERO_VALUE_STRING, maximum =
            MAX_INTEGER_VALUE_STRING)
    private int index;

    /**
     * Attribute type
     */
    @Schema(description = "Attribute type")
    private EnumDto type;

    /**
     * Minimum value
     */
    @DecimalMin(MIN_INTEGER_VALUE_STRING)
    @DecimalMax(MAX_INTEGER_VALUE_STRING)
    @Schema(description = "Minimum value", example = "0.0")
    private BigDecimal minValue;

    /**
     * Maximum value
     */
    @DecimalMin(MIN_INTEGER_VALUE_STRING)
    @DecimalMax(MAX_INTEGER_VALUE_STRING)
    @Schema(description = "Maximum value", example = "100")
    private BigDecimal maxValue;

    /**
     * Mean value
     */
    @DecimalMin(MIN_INTEGER_VALUE_STRING)
    @DecimalMax(MAX_INTEGER_VALUE_STRING)
    @Schema(description = "Mean value", example = "60")
    private BigDecimal meanValue;

    /**
     * Variance value
     */
    @DecimalMin(value = DECIMAL_MIN_ZERO)
    @DecimalMax(value = DECIMAL_MAX_ONE)
    @Schema(description = "Variance value", example = "0.1")
    private BigDecimal varianceValue;

    /**
     * Standard deviation value
     */
    @DecimalMin(value = DECIMAL_MIN_ZERO)
    @DecimalMax(value = DECIMAL_MAX_ONE)
    @Schema(description = "Standard deviation value", example = "0.01")
    private BigDecimal stdDevValue;

    /**
     * Frequency diagram values
     */
    @Schema(description = "Frequency diagram values")
    private List<FrequencyDiagramDataDto> frequencyDiagramValues;
}
