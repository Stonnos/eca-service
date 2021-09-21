package com.ecaservice.web.dto.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

import static com.ecaservice.web.dto.util.FieldConstraints.DATE_TIME_PATTERN;

/**
 * Email template parameter model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Email template parameter model")
public class EmailTemplateParameterDto {

    /**
     * ID
     */
    @Schema(description = "ID")
    private Long id;

    /**
     * Parameter creation date
     */
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Schema(description = "Parameter creation date")
    private LocalDateTime created;

    /**
     * Parameter name
     */
    @Schema(description = "Parameter name")
    private String parameterName;

    /**
     * Parameter description
     */
    @Schema(description = "Parameter description")
    private String description;
}
