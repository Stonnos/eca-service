package com.ecaservice.web.dto.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

import static com.ecaservice.web.dto.util.FieldConstraints.DATE_TIME_PATTERN;
import static com.ecaservice.web.dto.util.FieldConstraints.LOCAL_DATE_TIME_MAX_LENGTH;
import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_255;
import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LONG_VALUE_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.VALUE_1_STRING;

/**
 * Email template model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Email template model")
public class EmailTemplateDto {

    /**
     * ID
     */
    @Schema(description = "ID", example = "1", minimum = VALUE_1_STRING, maximum = MAX_LONG_VALUE_STRING)
    private Long id;

    /**
     * Template creation date
     */
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Schema(description = "Template creation date", type = "string", example = "2021-07-01 14:00:00",
            maxLength = LOCAL_DATE_TIME_MAX_LENGTH)
    private LocalDateTime created;

    /**
     * Template code
     */
    @Schema(description = "Template code", example = "NEW_EXPERIMENT", maxLength = MAX_LENGTH_255)
    private String code;

    /**
     * Template description
     */
    @Schema(description = "Template description", example = "New experiment", maxLength = MAX_LENGTH_255)
    private String description;

    /**
     * Template subject
     */
    @Schema(description = "Template subject", example = "New experiment request", maxLength = MAX_LENGTH_255)
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
