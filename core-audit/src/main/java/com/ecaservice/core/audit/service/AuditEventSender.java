package com.ecaservice.core.audit.service;

import com.ecaservice.audit.dto.AuditEventRequest;

/**
 * Interface for sending audit events to specified channel.
 *
 * @author Roman Batygin
 */
public interface AuditEventSender {

    /**
     * Sends audit event request.
     *
     * @param auditEventRequest audit event request
     */
    void sendEvent(AuditEventRequest auditEventRequest);
}
