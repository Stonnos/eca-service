package com.ecaservice.audit.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.time.LocalDateTime;

/**
 * Audit event request model.
 *
 * @author Roman Batygin
 */
@Data
@Builder
public class AuditEventRequest {

    @Tolerate
    public AuditEventRequest() {
        //default constructor
    }

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
