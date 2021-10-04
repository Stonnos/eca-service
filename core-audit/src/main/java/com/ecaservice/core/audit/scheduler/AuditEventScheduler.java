package com.ecaservice.core.audit.scheduler;

import com.ecaservice.core.audit.config.AuditProperties;
import com.ecaservice.core.audit.service.AuditEventRedeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Audit event scheduler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@ConditionalOnProperty(value = "audit.redelivery", havingValue = "true")
@RequiredArgsConstructor
public class AuditEventScheduler {

    private final AuditProperties auditProperties;
    private final ThreadPoolTaskScheduler auditThreadPoolTaskScheduler;
    private final AuditEventRedeliveryService auditEventRedeliveryService;

    /**
     * Starts audit events redelivery job.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        log.info("Starting to initialize audit redelivery job");
        auditThreadPoolTaskScheduler.scheduleWithFixedDelay(auditEventRedeliveryService::processNotSentEvents,
                auditProperties.getRedeliveryIntervalMillis());
        log.info("Audit redelivery job has been started");
    }
}
