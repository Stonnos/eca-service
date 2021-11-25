package com.ecaservice.web.dto.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

import static com.ecaservice.web.dto.util.FieldConstraints.DATE_TIME_PATTERN;
import static com.ecaservice.web.dto.util.FieldConstraints.LOCAL_DATE_TIME_MAX_LENGTH;
import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_255;
import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LONG_VALUE_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.VALUE_1_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.ZERO_VALUE_STRING;

/**
 * Classifiers configuration dto model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Classifiers configuration dto model")
public class ClassifiersConfigurationDto {

    @Schema(description = "Configuration id", example = "1", minimum = VALUE_1_STRING, maximum = MAX_LONG_VALUE_STRING)
    private Long id;

    /**
     * Configuration name
     */
    @Schema(description = "Configuration name", example = "Default configuration", maxLength = MAX_LENGTH_255)
    private String configurationName;

    /**
     * Configuration created date
     */
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Schema(description = "Configuration creation date", type = "string", example = "2021-07-01 14:00:00",
            maxLength = LOCAL_DATE_TIME_MAX_LENGTH)
    private LocalDateTime creationDate;

    /**
     * User name
     */
    @Schema(description = "User name", example = "admin", maxLength = MAX_LENGTH_255)
    private String createdBy;

    /**
     * Configuration updated date
     */
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Schema(description = "Configuration updated date", type = "string", example = "2021-07-01 14:00:00",
            maxLength = LOCAL_DATE_TIME_MAX_LENGTH)
    private LocalDateTime updated;

    /**
     * Is active?
     */
    @Schema(description = "Is active?")
    private boolean active;

    /**
     * Is build in?
     */
    @Schema(description = "Is build in?")
    private boolean buildIn;

    /**
     * Classifiers options count associated with configuration
     */
    @Schema(description = "Classifiers options count associated with configuration", example = "25",
            minimum = ZERO_VALUE_STRING, maximum = MAX_LONG_VALUE_STRING)
    private long classifiersOptionsCount;
}
