package com.ecaservice.core.audit.service;

import com.ecaservice.core.audit.entity.EventType;
import com.ecaservice.core.audit.model.AuditCodeModel;
import com.ecaservice.core.audit.model.AuditEventTemplateModel;

/**
 * Interface to manage with audit codes store.
 *
 * @author Roman Batygin
 */
public interface AuditCodeStore {

    /**
     * Gets audit code model.
     *
     * @param code - code value
     * @return audit code model
     */
    AuditCodeModel getAuditCode(String code);

    /**
     * Gets audit event template model.
     *
     * @param auditCode - audit code
     * @param eventType - event type
     * @return audit event template model
     */
    AuditEventTemplateModel getAuditEventTemplate(String auditCode, EventType eventType);
}
