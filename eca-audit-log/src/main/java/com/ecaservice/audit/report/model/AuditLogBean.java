package com.ecaservice.audit.report.model;

import lombok.Data;

/**
 * Audit log bean.
 *
 * @author Roman Batygin
 */
@Data
public class AuditLogBean {

    /**
     * Audit event id.
     */
    private String eventId;

    /**
     * Correlation id.
     */
    private String correlationId;

    /**
     * Audit message
     */
    private String message;

    /**
     * Event initiator
     */
    private String initiator;

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
    private String eventDate;
}
