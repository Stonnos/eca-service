package com.ecaservice.core.audit.service;

import com.ecaservice.audit.dto.AuditEventRequest;
import com.ecaservice.core.redelivery.annotation.Retry;
import com.ecaservice.core.redelivery.annotation.Retryable;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import static com.ecaservice.core.redelivery.config.RedeliveryCoreAutoConfiguration.FEIGN_EXCEPTION_STRATEGY;

/**
 * Service to sent audit events via REST API.
 *
 * @author Roman Batygin
 */
@Slf4j
@Retryable
@Service
@ConditionalOnProperty(value = "audit.sender.type", havingValue = "web", matchIfMissing = true)
@RequiredArgsConstructor
public class AuditEventHttpSender implements AuditEventSender {

    private final AuditEventClient auditEventClient;

    /**
     * Initialization method.
     */
    @PostConstruct
    public void init() {
        log.info("Audit events http sender has been configured");
    }

    @Override
    @Retry(value = "auditRequest", exceptionStrategy = FEIGN_EXCEPTION_STRATEGY,
            requestIdKey = "#auditEventRequest.eventId")
    public void sendAuditEvent(AuditEventRequest auditEventRequest) {
        log.info("Starting to send audit event [{}] with code [{}], type [{}]", auditEventRequest.getEventId(),
                auditEventRequest.getCode(), auditEventRequest.getEventType());
        auditEventClient.sendEvent(auditEventRequest);
        log.info("Audit event [{}] with code [{}], type [{}] has been sent", auditEventRequest.getEventId(),
                auditEventRequest.getCode(), auditEventRequest.getEventType());
    }
}
