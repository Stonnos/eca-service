package com.ecaservice.core.audit.model;

import com.ecaservice.audit.dto.EventType;
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
     * Audit message template.
     */
    private String messageTemplate;
}
