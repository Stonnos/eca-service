package com.ecaservice.core.audit.service;

import com.ecaservice.audit.dto.AuditEventRequest;
import com.ecaservice.core.audit.entity.EventStatus;
import com.ecaservice.core.audit.event.AuditEvent;
import com.ecaservice.core.audit.mapping.AuditMapper;
import com.ecaservice.core.audit.model.AuditEventTemplateModel;
import com.ecaservice.core.audit.repository.AuditEventRequestRepository;
import com.ecaservice.core.audit.service.template.AuditTemplateProcessorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service to sending audit events.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditEventService {

    private final AuditEventSender auditEventSender;
    private final AuditEventTemplateStore auditEventTemplateStore;
    private final AuditTemplateProcessorService auditTemplateProcessorService;
    private final AuditMapper auditMapper;
    private final AuditEventRequestRepository auditEventRequestRepository;

    /**
     * Send audit event.
     *
     * @param auditEvent - audit event
     */
    public void audit(AuditEvent auditEvent) {
        String eventId = UUID.randomUUID().toString();
        log.debug("Audit event [{}] type [{}] with event id [{}]", auditEvent.getAuditCode(),
                auditEvent.getEventType(), eventId);
        try {
            AuditEventTemplateModel auditEventTemplate =
                    auditEventTemplateStore.getAuditEventTemplate(auditEvent.getAuditCode(), auditEvent.getEventType());
            if (!Boolean.TRUE.equals(auditEventTemplate.getAuditCode().isEnabled())) {
                log.warn("Audit code [{}] is disabled", auditEvent.getAuditCode());
            } else {
                String message = auditTemplateProcessorService.process(auditEvent.getAuditCode(),
                        auditEvent.getEventType(), auditEvent.getAuditContextParams());
                AuditEventRequest auditEventRequest = auditMapper.map(auditEventTemplate);
                auditEventRequest.setEventId(eventId);
                auditEventRequest.setMessage(message);
                auditEventRequest.setInitiator(auditEvent.getInitiator());
                auditEventRequest.setEventDate(LocalDateTime.now());
                sendAuditEvent(auditEventRequest);
            }
        } catch (Exception ex) {
            log.error("There was an error while process audit event [{}]: {}", eventId, ex.getMessage());
        }
    }

    private void sendAuditEvent(AuditEventRequest auditEventRequest) {
        log.info("Starting to send audit event [{}] with code [{}], type [{}]", auditEventRequest.getEventId(),
                auditEventRequest.getCode(), auditEventRequest.getEventType());
        var auditEventRequestEntity = auditMapper.map(auditEventRequest);
        try {
            auditEventSender.sendEvent(auditEventRequest);
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
