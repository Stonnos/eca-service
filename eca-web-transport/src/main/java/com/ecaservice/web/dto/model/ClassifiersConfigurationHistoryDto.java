package com.ecaservice.web.dto.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

import static com.ecaservice.web.dto.util.FieldConstraints.*;

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
    @Schema(description = "ID", example = "1", required = true, minimum = VALUE_1_STRING,
            maximum = MAX_LONG_VALUE_STRING)
    private Long id;

    /**
     * Action type
     */
    @Schema(description = "Action type", required = true)
    private EnumDto actionType;

    /**
     * Message text
     */
    @Schema(description = "Message text", required = true, example = "Message text")
    private String messageText;

    /**
     * Creation date
     */
    @Schema(description = "Creation date", required = true, type = "string", example = "2021-07-01 14:00:00",
            maxLength = LOCAL_DATE_TIME_MAX_LENGTH)
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createdAt;

    /**
     * User name
     */
    @Schema(description = "User name", example = "admin", required = true, maxLength = MAX_LENGTH_255)
    private String createdBy;
}
