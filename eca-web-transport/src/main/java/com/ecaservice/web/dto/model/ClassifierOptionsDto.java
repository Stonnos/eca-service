package com.ecaservice.web.dto.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

import static com.ecaservice.web.dto.util.FieldConstraints.DATE_TIME_PATTERN;

/**
 * Classifier input options dto model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Classifier json input options model")
public class ClassifierOptionsDto {

    @Schema(description = "Options id")
    private Long id;

    /**
     * Options name
     */
    @Schema(description = "Options name", required = true)
    private String optionsName;

    /**
     * Creation date
     */
    @Schema(description = "Creation date", required = true, type = "string", example = "2021-07-01 14:00:00")
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime creationDate;

    /**
     * User name
     */
    @Schema(description = "User name")
    private String createdBy;

    /**
     * Json config
     */
    @Schema(description = "Json config", required = true)
    private String config;
}
