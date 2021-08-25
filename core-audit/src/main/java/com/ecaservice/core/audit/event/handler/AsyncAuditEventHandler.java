package com.ecaservice.core.audit.event.handler;

import com.ecaservice.core.audit.event.AuditEvent;
import com.ecaservice.core.audit.service.AuditEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import static com.ecaservice.core.audit.config.AuditCoreConfiguration.AUDIT_EVENT_THREAD_POOL_TASK_EXECUTOR;

/**
 * Service for handling asynchronous audit events.
 *
 * @author Roman Batygin
 */
@Service
@ConditionalOnProperty(value = "audit.asyncEvents", havingValue = "true")
@RequiredArgsConstructor
public class AsyncAuditEventHandler {

    private final AuditEventService auditEventService;

    @Async(AUDIT_EVENT_THREAD_POOL_TASK_EXECUTOR)
    @EventListener
    public void handleAuditEvent(AuditEvent auditEvent) {
        auditEventService.audit(auditEvent);
    }
}
