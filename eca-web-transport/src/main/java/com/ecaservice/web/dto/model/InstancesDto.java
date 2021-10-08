package com.ecaservice.web.dto.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

import static com.ecaservice.web.dto.util.FieldConstraints.DATE_TIME_PATTERN;

/**
 * Instances dto model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Instances model")
public class InstancesDto {

    @Schema(description = "Instances id", example = "1")
    private Long id;

    /**
     * Instances name
     */
    @Schema(description = "Table name", example = "iris")
    private String tableName;

    /**
     * Instances size
     */
    @Schema(description = "Instances number", example = "150")
    private Integer numInstances;

    /**
     * Attributes number
     */
    @Schema(description = "Attributes number", example = "5")
    private Integer numAttributes;

    /**
     * Instances created date
     */
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Schema(description = "Instances creation date", type = "string", example = "2021-07-01 14:00:00")
    private LocalDateTime created;

    /**
     * User name
     */
    @Schema(description = "User name", example = "admin")
    private String createdBy;
}
