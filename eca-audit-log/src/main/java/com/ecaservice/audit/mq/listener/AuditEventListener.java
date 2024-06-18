package com.ecaservice.audit.mq.listener;

import com.ecaservice.audit.dto.AuditEventRequest;
import com.ecaservice.audit.service.AuditLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * Audit event message listener.
 *
 * @author Roman Batygin
 */
@Slf4j
@Validated
@Component
@ConditionalOnProperty(value = "audit.rabbit.enabled", havingValue = "true")
@RequiredArgsConstructor
public class AuditEventListener {

    private final AuditLogService auditLogService;

    /**
     * Handles audit event.
     *
     * @param auditEventRequest - audit event request
     */
    @RabbitListener(queues = "${audit.rabbit.queueName}")
    public void handleAuditEvent(@Valid AuditEventRequest auditEventRequest) {
        log.info("Received audit event [{}] message from mq: {}", auditEventRequest.getEventId(), auditEventRequest);
        auditLogService.save(auditEventRequest);
        log.info("Audit event request [{}] mq message has been processed", auditEventRequest.getEventId());
    }
}
