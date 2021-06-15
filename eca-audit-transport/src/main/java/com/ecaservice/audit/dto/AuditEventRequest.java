package com.ecaservice.audit.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

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
    private String groupCode;

    /**
     * Audit group title
     */
    private String groupTitle;

    /**
     * Audit code
     */
    @NotEmpty
    private String code;

    /**
     * Audit code title
     */
    private String codeTitle;

    /**
     * Event date
     */
    @NotNull
    private LocalDateTime eventDate;
}
