package com.ecaservice.web.dto.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

import static com.ecaservice.web.dto.util.FieldConstraints.DATE_TIME_PATTERN;

/**
 * Audit log model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Audit log model")
public class AuditLogDto {

    /**
     * Audit event id.
     */
    @Schema(description = "Audit event id", example = "1d2de514-3a87-4620-9b97-c260e24340de")
    private String eventId;

    /**
     * Audit message
     */
    @Schema(description = "Audit message", example = "Some action")
    private String message;

    /**
     * Event initiator
     */
    @Schema(description = "Event initiator", example = "user")
    private String initiator;

    /**
     * Audit group
     */
    @Schema(description = "Audit group", example = "USER_ACTIONS")
    private String groupCode;

    /**
     * Audit group title
     */
    @Schema(description = "Audit group title", example = "User actions")
    private String groupTitle;

    /**
     * Audit code
     */
    @Schema(description = "Audit code", example = "LOGIN")
    private String code;

    /**
     * Audit code title
     */
    @Schema(description = "Audit code title", example = "User logged in")
    private String codeTitle;

    /**
     * Event date
     */
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Schema(description = "Event date", type = "string", example = "2021-07-01 14:00:00")
    private LocalDateTime eventDate;
}
