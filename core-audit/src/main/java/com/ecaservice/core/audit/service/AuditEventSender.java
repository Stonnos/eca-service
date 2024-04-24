package com.ecaservice.core.audit.service;

import com.ecaservice.audit.dto.AuditEventRequest;

/**
 * Service to sent audit events.
 *
 * @author Roman Batygin
 */
public interface AuditEventSender {

    /**
     * Sent audit event request.
     *
     * @param auditEventRequest - audit event request
     */
    void sendAuditEvent(AuditEventRequest auditEventRequest);
}
