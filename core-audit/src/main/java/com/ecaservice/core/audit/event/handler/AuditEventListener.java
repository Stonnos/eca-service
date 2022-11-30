package com.ecaservice.core.audit.event.handler;

import com.ecaservice.core.audit.event.AuditEvent;
import com.ecaservice.core.audit.service.impl.SimpleAuditEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Service for handling audit events.
 *
 * @author Roman Batygin
 */
@Service
@RequiredArgsConstructor
public class AuditEventListener {

    private final SimpleAuditEventService auditEventService;

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
