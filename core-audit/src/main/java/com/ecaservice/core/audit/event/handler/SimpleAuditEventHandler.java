package com.ecaservice.core.audit.event.handler;

import com.ecaservice.core.audit.event.AuditEvent;
import com.ecaservice.core.audit.service.AuditEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Service for handling audit events.
 *
 * @author Roman Batygin
 */
@Service
@ConditionalOnProperty(value = "audit.asyncEvents", havingValue = "false", matchIfMissing = true)
@RequiredArgsConstructor
public class SimpleAuditEventHandler {

    private final AuditEventService auditEventService;

    /**
     * Handles audit event.
     *
     * @param auditEvent - audit event
     */
    @EventListener
    public void handleAuditEvent(AuditEvent auditEvent) {
        auditEventService.audit(auditEvent);
    }
}
