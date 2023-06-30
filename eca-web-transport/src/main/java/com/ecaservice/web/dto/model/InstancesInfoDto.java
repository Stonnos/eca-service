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
import static com.ecaservice.web.dto.util.FieldConstraints.MIN_NUM_CLASSES_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.VALUE_1_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.VALUE_2_STRING;

/**
 * Instances info dto model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Classifier training data model")
public class InstancesInfoDto {

    /**
     * ID
     */
    @Schema(description = "ID", example = "1", minimum = VALUE_1_STRING, maximum = MAX_LONG_VALUE_STRING)
    private Long id;

    /**
     * Instances name
     */
    @Schema(description = "Instances name", example = "iris", maxLength = MAX_LENGTH_255)
    private String relationName;

    /**
     * Instances size
     */
    @Schema(description = "Instances number", example = "150", minimum = VALUE_2_STRING,
            maximum = MAX_INTEGER_VALUE_STRING)
    private Integer numInstances;

    /**
     * Attributes number
     */
    @Schema(description = "Attributes number", example = "5", minimum = VALUE_2_STRING,
            maximum = MAX_INTEGER_VALUE_STRING)
    private Integer numAttributes;

    /**
     * Classes number
     */
    @Schema(description = "Classes number", example = "4", minimum = MIN_NUM_CLASSES_STRING,
            maximum = MAX_INTEGER_VALUE_STRING)
    private Integer numClasses;

    /**
     * Class attribute name
     */
    @Schema(description = "Class name", example = "class", maxLength = MAX_LENGTH_255)
    private String className;

    /**
     * Created date
     */
    @Schema(description = "Created date", type = "string", example = "2021-07-01 14:00:00",
            maxLength = LOCAL_DATE_TIME_MAX_LENGTH)
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createdDate;
}
