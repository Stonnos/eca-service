package com.ecaservice.core.audit.service;

import com.ecaservice.audit.dto.EventType;
import com.ecaservice.core.audit.model.AuditEventTemplateModel;

/**
 * Interface to manage with audit event templates store.
 *
 * @author Roman Batygin
 */
public interface AuditEventTemplateStore {

    /**
     * Gets audit event template model.
     *
     * @param auditCode - audit code
     * @param eventType - event type
     * @return audit event template model
     */
    AuditEventTemplateModel getAuditEventTemplate(String auditCode, EventType eventType);
}
