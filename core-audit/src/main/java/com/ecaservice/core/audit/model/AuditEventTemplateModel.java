package com.ecaservice.core.audit.model;

import com.ecaservice.core.audit.entity.EventType;
import lombok.Data;

/**
 * Audit event template model.
 *
 * @author Roman Batygin
 */
@Data
public class AuditEventTemplateModel {

    /**
     * Audit code.
     */
    private AuditCodeModel auditCode;

    /**
     * Audit event type.
     */
    private EventType eventType;

    /**
     * Template title
     */
    private String title;

    /**
     * Audit message template.
     */
    private String messageTemplate;
}
