package com.ecaservice.web.dto.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

import static com.ecaservice.web.dto.util.FieldConstraints.DATE_TIME_PATTERN;

/**
 * Classifiers configuration dto model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Classifiers configuration dto model")
public class ClassifiersConfigurationDto {

    @Schema(description = "Configuration id")
    private Long id;

    /**
     * Configuration name
     */
    @Schema(description = "Configuration name")
    private String configurationName;

    /**
     * Configuration created date
     */
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Schema(description = "Configuration creation date")
    private LocalDateTime creationDate;

    /**
     * User name
     */
    @Schema(description = "User name")
    private String createdBy;

    /**
     * Configuration updated date
     */
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Schema(description = "Configuration updated date")
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
    @Schema(description = "Classifiers options count associated with configuration")
    private long classifiersOptionsCount;
}
