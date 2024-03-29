package com.ecaservice.web.dto.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

import static com.ecaservice.web.dto.util.FieldConstraints.DATE_TIME_PATTERN;
import static com.ecaservice.web.dto.util.FieldConstraints.LOCAL_DATE_TIME_MAX_LENGTH;
import static com.ecaservice.web.dto.util.FieldConstraints.MAX_INTEGER_VALUE_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_255;
import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LONG_VALUE_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.UUID_MAX_LENGTH;
import static com.ecaservice.web.dto.util.FieldConstraints.VALUE_1_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.ZERO_VALUE_STRING;

/**
 * Instances dto model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Instances model")
public class InstancesDto {

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
     * Instances created date
     */
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Schema(description = "Instances creation date", type = "string", example = "2021-07-01 14:00:00",
            maxLength = LOCAL_DATE_TIME_MAX_LENGTH)
    private LocalDateTime created;

    /**
     * User name
     */
    @Schema(description = "User name", example = "admin", maxLength = MAX_LENGTH_255)
    private String createdBy;
}
