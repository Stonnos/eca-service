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
 * User notification dto.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "User notification")
public class UserNotificationDto {

    @Schema(description = "Notification id", example = "1", minimum = VALUE_1_STRING, maximum = MAX_LONG_VALUE_STRING)
    private Long id;

    /**
     * Message type
     */
    @Schema(description = "Message type", example = "CLASSIFIER_CONFIGURATION_CHANGE", maxLength = MAX_LENGTH_255)
    private String messageType;

    /**
     * Message text
     */
    @Schema(description = "Message text", example = "Message text", maxLength = MAX_LENGTH_255)
    private String messageText;

    /**
     * Initiator user
     */
    @Schema(description = "Initiator user", example = "admin", maxLength = MAX_LENGTH_255)
    private String initiator;

    /**
     * Message status
     */
    @Schema(description = "Message status")
    private EnumDto messageStatus;

    /**
     * Created date
     */
    @Schema(description = "Created date", type = "string", example = "2021-07-01 14:00:00",
            maxLength = LOCAL_DATE_TIME_MAX_LENGTH)
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime created;

    /**
     * Notification parameters list
     */
    @Schema(description = "Notification parameters list")
    private List<UserNotificationParameterDto> parameters;
}
