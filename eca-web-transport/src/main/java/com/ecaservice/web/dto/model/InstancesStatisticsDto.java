package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import static com.ecaservice.web.dto.util.FieldConstraints.MAX_INTEGER_VALUE_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_255;
import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LONG_VALUE_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.UUID_MAX_LENGTH;
import static com.ecaservice.web.dto.util.FieldConstraints.VALUE_1_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.ZERO_VALUE_STRING;

/**
 * Instances statistics dto model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Instances statistics model")
public class InstancesStatisticsDto {

    @Schema(description = "Instances id", example = "1", minimum = VALUE_1_STRING, maximum = MAX_LONG_VALUE_STRING)
    private Long id;

    /**
     * Instances uuid
     */
    @Schema(description = "Instances uuid", example = "1d2de514-3a87-4620-9b97-c260e24340de",
            maxLength = UUID_MAX_LENGTH)
    private String uuid;

    /**
     * Instances name
     */
    @Schema(description = "Instances name", example = "iris", maxLength = MAX_LENGTH_255)
    private String relationName;

    /**
     * Instances size
     */
    @Schema(description = "Instances number", example = "150", minimum = ZERO_VALUE_STRING,
            maximum = MAX_INTEGER_VALUE_STRING)
    private Integer numInstances;

    /**
     * Attributes number
     */
    @Schema(description = "Attributes number", example = "5", minimum = ZERO_VALUE_STRING,
            maximum = MAX_INTEGER_VALUE_STRING)
    private Integer numAttributes;

    /**
     * Class name
     */
    @Schema(description = "Class name", example = "class", maxLength = MAX_LENGTH_255)
    private String className;

    /**
     * Classes number
     */
    @Schema(description = "Classes number", example = "4", minimum = ZERO_VALUE_STRING,
            maximum = MAX_INTEGER_VALUE_STRING)
    private Integer numClasses;

    /**
     * Numeric attributes number
     */
    @Schema(description = "Numeric attributes number", example = "4", minimum = ZERO_VALUE_STRING,
            maximum = MAX_INTEGER_VALUE_STRING)
    private Integer numNumericAttributes;

    /**
     * Nominal attributes number
     */
    @Schema(description = "Nominal attributes number", example = "2", minimum = ZERO_VALUE_STRING,
            maximum = MAX_INTEGER_VALUE_STRING)
    private Integer numNominalAttributes;

    /**
     * Date attributes number
     */
    @Schema(description = "Date attributes number", example = "0", minimum = ZERO_VALUE_STRING,
            maximum = MAX_INTEGER_VALUE_STRING)
    private Integer numDateAttributes;
}
