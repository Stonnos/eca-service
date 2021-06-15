package com.ecaservice.audit.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static com.ecaservice.audit.dto.FieldConstraints.DATE_TIME_PATTERN;
import static com.ecaservice.audit.dto.FieldConstraints.MAX_LENGTH_255;

/**
 * Audit event request model.
 *
 * @author Roman Batygin
 */
@Data
public class AuditEventRequest {

    /**
     * Audit event id.
     */
    @NotEmpty
    @Size(max = MAX_LENGTH_255)
    private String eventId;

    /**
     * Audit message
     */
    @NotEmpty
    private String message;

    /**
     * Event initiator
     */
    @NotEmpty
    @Size(max = MAX_LENGTH_255)
    private String initiator;

    /**
     * Event type
     */
    @NotNull
    private EventType eventType;

    /**
     * Audit group
     */
    @NotEmpty
    @Size(max = MAX_LENGTH_255)
    private String groupCode;

    /**
     * Audit group title
     */
    @Size(max = MAX_LENGTH_255)
    private String groupTitle;

    /**
     * Audit code
     */
    @NotEmpty
    @Size(max = MAX_LENGTH_255)
    private String code;

    /**
     * Audit code title
     */
    @Size(max = MAX_LENGTH_255)
    private String codeTitle;

    /**
     * Event date
     */
    @NotNull
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime eventDate;
}
