package com.ecaservice.core.audit.event.handler;

import com.ecaservice.core.audit.event.AuditEvent;
import com.ecaservice.core.audit.service.AuditEventService;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for handling asynchronous audit events.
 *
 * @author Roman Batygin
 */
@Slf4j
public class DefaultAuditEventHandler {

    private final AuditEventService auditEventService;

    /**
     * Constructor with spring dependency injection.
     *
     * @param auditEventService - audit event service
     */
    public DefaultAuditEventHandler(AuditEventService auditEventService) {
        this.auditEventService = auditEventService;
    }

    /**
     * Handles audit event.
     *
     * @param auditEvent - audit event
     */
    public void handleAuditEvent(AuditEvent auditEvent) {
        log.debug("Starting to handle audit event [{}] with type [{}]", auditEvent.getAuditCode(),
                auditEvent.getEventType());
        auditEventService.audit(auditEvent);
        log.debug("Audit event [{}] with type [{}] has been processed", auditEvent.getAuditCode(),
                auditEvent.getEventType());
    }
}
