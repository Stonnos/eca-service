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

/**
 * Classifiers configuration history dto model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Classifiers configuration history model")
public class ClassifiersConfigurationHistoryDto {

    /**
     * ID
     */
    @Schema(description = "ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED, minimum = VALUE_1_STRING,
            maximum = MAX_LONG_VALUE_STRING)
    private Long id;

    /**
     * Action type
     */
    @Schema(description = "Action type", requiredMode = Schema.RequiredMode.REQUIRED)
    private EnumDto actionType;

    /**
     * Message text
     */
    @Schema(description = "Message text", requiredMode = Schema.RequiredMode.REQUIRED, example = "Message text")
    private String messageText;

    /**
     * Creation date
     */
    @Schema(description = "Creation date", requiredMode = Schema.RequiredMode.REQUIRED, type = "string",
            example = "2021-07-01 14:00:00",
            maxLength = LOCAL_DATE_TIME_MAX_LENGTH)
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createdAt;

    /**
     * User name
     */
    @Schema(description = "User name", example = "admin", requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = MAX_LENGTH_255)
    private String createdBy;
}
