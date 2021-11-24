package com.ecaservice.audit.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static com.ecaservice.audit.dto.FieldConstraints.DATE_TIME_PATTERN;
import static com.ecaservice.audit.dto.FieldConstraints.LOCAL_DATE_TIME_MAX_LENGTH;
import static com.ecaservice.audit.dto.FieldConstraints.MAX_LENGTH_255;
import static com.ecaservice.audit.dto.FieldConstraints.VALUE_1;

/**
 * Audit event request model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Audit event request mode")
public class AuditEventRequest {

    /**
     * Audit event id.
     */
    @NotEmpty
    @Size(min = VALUE_1, max = MAX_LENGTH_255)
    @Schema(description = "Audit event id")
    private String eventId;

    /**
     * Audit message
     */
    @NotEmpty
    @Size(min = VALUE_1)
    @Schema(description = "Audit message")
    private String message;

    /**
     * Event initiator
     */
    @NotEmpty
    @Size(min = VALUE_1, max = MAX_LENGTH_255)
    @Schema(description = "Event initiator")
    private String initiator;

    /**
     * Event type
     */
    @NotNull
    @Schema(description = "Event type", minLength = VALUE_1, maxLength = MAX_LENGTH_255)
    private EventType eventType;

    /**
     * Audit group
     */
    @NotEmpty
    @Size(min = VALUE_1, max = MAX_LENGTH_255)
    @Schema(description = "Audit group")
    private String groupCode;

    /**
     * Audit group title
     */
    @Size(max = MAX_LENGTH_255)
    @Schema(description = "Audit group title")
    private String groupTitle;

    /**
     * Audit code
     */
    @NotEmpty
    @Size(min = VALUE_1, max = MAX_LENGTH_255)
    @Schema(description = "Audit code")
    private String code;

    /**
     * Audit code title
     */
    @Size(max = MAX_LENGTH_255)
    @Schema(description = "Audit code title")
    private String codeTitle;

    /**
     * Event date
     */
    @NotNull
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Schema(description = "Event date", pattern = DATE_TIME_PATTERN, maxLength = LOCAL_DATE_TIME_MAX_LENGTH)
    private LocalDateTime eventDate;
}
