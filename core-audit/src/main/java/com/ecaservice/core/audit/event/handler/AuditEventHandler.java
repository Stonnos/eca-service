package com.ecaservice.core.audit.event.handler;

import com.ecaservice.core.audit.event.AuditEvent;
import com.ecaservice.core.audit.service.AuditEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Service for handling audit events.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@ConditionalOnProperty(value = "audit.asyncEvents", havingValue = "false")
@RequiredArgsConstructor
public class AuditEventHandler {

    private final AuditEventService auditEventService;

    /**
     * Handles audit event.
     *
     * @param auditEvent - audit event
     */
    @EventListener
    public void handleAuditEvent(AuditEvent auditEvent) {
        log.debug("Starting to handle audit event [{}] with type [{}]", auditEvent.getAuditCode(),
                auditEvent.getEventType());
        auditEventService.audit(auditEvent);
        log.debug("Audit event [{}] with type [{}] has been processed", auditEvent.getAuditCode(),
                auditEvent.getEventType());
    }
}
