package com.ecaservice.core.audit.event.handler;

import com.ecaservice.core.audit.event.AuditEvent;
import com.ecaservice.core.audit.service.AuditEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Service
@ConditionalOnProperty(value = "audit.asyncEvents", havingValue = "true")
@RequiredArgsConstructor
public class AsyncAuditEventHandler {

    private final AuditEventService auditEventService;

    /**
     * Handles audit event.
     *
     * @param auditEvent - audit event
     */
    @Async(AUDIT_EVENT_THREAD_POOL_TASK_EXECUTOR)
    @EventListener
    public void handleAuditEvent(AuditEvent auditEvent) {
        log.debug("Starting to handle audit event [{}] with type [{}]", auditEvent.getAuditCode(),
                auditEvent.getEventType());
        auditEventService.audit(auditEvent);
        log.debug("Audit event [{}] with type [{}] has been processed", auditEvent.getAuditCode(),
                auditEvent.getEventType());
    }
}
