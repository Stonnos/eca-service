package com.ecaservice.core.audit.service;

import com.ecaservice.audit.dto.AuditEventRequest;
import com.ecaservice.core.redelivery.annotation.Retry;
import com.ecaservice.core.redelivery.annotation.Retryable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service to sent audit events.
 *
 * @author Roman Batygin
 */
@Slf4j
@Retryable
@Service
@RequiredArgsConstructor
public class AuditEventSender {

    private final AuditEventClient auditEventClient;

    /**
     * Sent audit event request.
     *
     * @param auditEventRequest - audit event request
     */
    @Retry(value = "AUDIT_REQUEST", exceptionStrategy = "feignExceptionStrategy")
    public void sendAuditEvent(AuditEventRequest auditEventRequest) {
        log.info("Starting to send audit event [{}] with code [{}], type [{}]", auditEventRequest.getEventId(),
                auditEventRequest.getCode(), auditEventRequest.getEventType());
        auditEventClient.sendEvent(auditEventRequest);
        log.info("Audit event [{}] with code [{}], type [{}] has been sent", auditEventRequest.getEventId(),
                auditEventRequest.getCode(), auditEventRequest.getEventType());
    }
}
