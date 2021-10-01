package com.ecaservice.core.audit.service;

import com.ecaservice.audit.dto.AuditEventRequest;
import com.ecaservice.core.audit.entity.AuditEventRequestEntity;
import com.ecaservice.core.audit.entity.EventStatus;
import com.ecaservice.core.audit.repository.AuditEventRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service to sent audit events.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditEventSender {

    private final AuditEventClient auditEventClient;
    private final AuditEventRequestRepository auditEventRequestRepository;

    /**
     * Sent audit event request.
     *
     * @param auditEventRequest       - audit event request
     * @param auditEventRequestEntity - audit event request entity
     */
    public void sendAuditEvent(AuditEventRequest auditEventRequest, AuditEventRequestEntity auditEventRequestEntity) {
        log.info("Starting to send audit event [{}] with code [{}], type [{}]", auditEventRequest.getEventId(),
                auditEventRequest.getCode(), auditEventRequest.getEventType());
        try {
            auditEventClient.sendEvent(auditEventRequest);
            auditEventRequestEntity.setEventStatus(EventStatus.SENT);
            log.info("Audit event [{}] with code [{}], type [{}] has been sent", auditEventRequest.getEventId(),
                    auditEventRequest.getCode(), auditEventRequest.getEventType());
        } catch (Exception ex) {
            log.error("There was an error while sending audit event [{}]: {}", auditEventRequest.getEventId(),
                    ex.getMessage());
            auditEventRequestEntity.setEventStatus(EventStatus.NOT_SENT);
        } finally {
            auditEventRequestRepository.save(auditEventRequestEntity);
        }
    }
}
