package com.ecaservice.audit.dto;

import lombok.Data;

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
    private String eventId;

    /**
     * Audit message
     */
    private String message;

    /**
     * Event initiator
     */
    private String initiator;

    /**
     * Event type
     */
    private EventType eventType;

    /**
     * Audit group
     */
    private String groupCode;

    /**
     * Audit group title
     */
    private String groupTitle;

    /**
     * Audit code
     */
    private String code;

    /**
     * Audit code title
     */
    private String codeTitle;

    /**
     * Event date
     */
    private LocalDateTime eventDate;
}
