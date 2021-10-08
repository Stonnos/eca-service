package com.ecaservice.web.dto.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

import static com.ecaservice.web.dto.util.FieldConstraints.DATE_TIME_PATTERN;

/**
 * Email template model.
 * @author Roman Batygin
 */
@Data
@Schema(description = "Email template model")
public class EmailTemplateDto {

    /**
     * ID
     */
    @Schema(description = "ID", example = "1")
    private Long id;

    /**
     * Template creation date
     */
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Schema(description = "Template creation date", type = "string", example = "2021-07-01 14:00:00")
    private LocalDateTime created;

    /**
     * Template code
     */
    @Schema(description = "Template code", example = "NEW_EXPERIMENT")
    private String code;

    /**
     * Template description
     */
    @Schema(description = "Template description", example = "New experiment")
    private String description;

    /**
     * Template subject
     */
    @Schema(description = "Template subject", example = "New experiment request")
    private String subject;

    /**
     * Template body
     */
    @Schema(description = "Template body", example = "some body")
    private String body;

    /**
     * Email template parameters
     */
    @Schema(description = "Email template parameters")
    private List<EmailTemplateParameterDto> parameters;
}
