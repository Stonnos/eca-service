package com.ecaservice.core.audit.service.impl;

import com.ecaservice.core.audit.event.AuditEvent;
import com.ecaservice.core.audit.service.AuditEventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import static com.ecaservice.core.audit.config.AuditCoreConfiguration.AUDIT_EVENT_THREAD_POOL_TASK_EXECUTOR;

/**
 * Service to sending asynchronous audit events.
 *
 * @author Roman Batygin
 */
@Slf4j
@ConditionalOnProperty(value = "audit.asyncEvents", havingValue = "true")
@Primary
@Service
public class AsyncAuditEventService implements AuditEventService {

    private final AuditEventService auditEventService;

    /**
     * Initialization method.
     */
    @PostConstruct
    public void init() {
        log.info("Async audit events service has been configured");
    }

    /**
     * Constructor with parameters
     *
     * @param auditEventService - simple audit event service
     */
    public AsyncAuditEventService(@Qualifier("simpleAuditEventService") AuditEventService auditEventService) {
        this.auditEventService = auditEventService;
    }

    @Override
    @Async(AUDIT_EVENT_THREAD_POOL_TASK_EXECUTOR)
    public void audit(AuditEvent auditEvent) {
        auditEventService.audit(auditEvent);
    }
}
