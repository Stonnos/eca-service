package com.ecaservice.core.audit.event.handler;

import com.ecaservice.core.audit.event.AuditEvent;
import com.ecaservice.core.audit.service.AuditEventService;
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
public class SimpleAuditEventHandler extends DefaultAuditEventHandler {

    /**
     * Constructor with spring dependency injection.
     *
     * @param auditEventService - audit event service
     */
    public SimpleAuditEventHandler(AuditEventService auditEventService) {
        super(auditEventService);
    }

    @Override
    @EventListener
    public void handleAuditEvent(AuditEvent auditEvent) {
        super.handleAuditEvent(auditEvent);
    }
}
