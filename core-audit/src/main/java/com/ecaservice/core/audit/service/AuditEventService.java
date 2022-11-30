package com.ecaservice.core.audit.service;

import com.ecaservice.core.audit.event.AuditEvent;

/**
 * Audit event service interface.
 *
 * @author Roman Batygin
 */
public interface AuditEventService {

    /**
     * Send audit event.
     *
     * @param auditEvent - audit event
     */
    void audit(AuditEvent auditEvent);
}
