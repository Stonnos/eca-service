package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

import static com.ecaservice.web.dto.util.FieldConstraints.MAX_INTEGER_VALUE_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_255;
import static com.ecaservice.web.dto.util.FieldConstraints.ZERO_VALUE_STRING;

/**
 * Form field dto model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Form field model")
public class FormFieldDto {

    /**
     * Field name
     */
    @Schema(description = "Field name", example = "field", maxLength = MAX_LENGTH_255)
    private String fieldName;

    /**
     * Field description
     */
    @Schema(description = "Field description", example = "Field description", maxLength = MAX_LENGTH_255)
    private String description;

    /**
     * Field order
     */
    @Schema(description = "Field order", example = "0", minimum = ZERO_VALUE_STRING, maximum = MAX_INTEGER_VALUE_STRING)
    private int fieldOrder;

    /**
     * Form field type
     */
    @Schema(description = "Form field type", example = "TEXT", maxLength = MAX_LENGTH_255)
    private FieldType fieldType;

    /**
     * Min. value
     */
    @Schema(description = "Minimum value", minimum = ZERO_VALUE_STRING, maximum = MAX_INTEGER_VALUE_STRING)
    private BigDecimal minValue;

    /**
     * Min. value inclusive?
     */
    @Schema(description = "Min. value inclusive?")
    private boolean minInclusive;

    /**
     * Max. value
     */
    @Schema(description = "Maximum value", minimum = ZERO_VALUE_STRING, maximum = MAX_INTEGER_VALUE_STRING)
    private BigDecimal maxValue;

    /**
     * Max. value inclusive?
     */
    @Schema(description = "Max. value inclusive?")
    private boolean maxInclusive;

    /**
     * Max length value
     */
    @Schema(description = "Maximum value length", example = "255", minimum = ZERO_VALUE_STRING,
            maximum = MAX_INTEGER_VALUE_STRING)
    private Integer maxLength;

    /**
     * Pattern value (regular expression)
     */
    @Schema(description = "Pattern value", maxLength = MAX_LENGTH_255)
    private String pattern;

    /**
     * Invalid pattern message
     */
    @Schema(description = "Invalid pattern message", maxLength = MAX_LENGTH_255)
    private String invalidPatternMessage;

    /**
     * Field dictionary
     */
    @Schema(description = "Field dictionary")
    private FieldDictionaryDto dictionary;

    /**
     * Place holder message
     */
    @Schema(description = "Place holder message", maxLength = MAX_LENGTH_255)
    private String placeHolder;

    /**
     * Default value
     */
    @Schema(description = "Default value", maxLength = MAX_LENGTH_255)
    private String defaultValue;
}
