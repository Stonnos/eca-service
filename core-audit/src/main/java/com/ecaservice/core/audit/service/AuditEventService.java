package com.ecaservice.core.audit.service;

import com.ecaservice.audit.dto.AuditEventRequest;
import com.ecaservice.audit.dto.EventType;
import com.ecaservice.core.audit.mapping.AuditMapper;
import com.ecaservice.core.audit.model.AuditContextParams;
import com.ecaservice.core.audit.model.AuditEventTemplateModel;
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

    /**
     * Send audit event.
     *
     * @param auditCode          - audit code
     * @param eventType          - event type
     * @param initiator          - event initiator
     * @param auditContextParams - audit context params
     */
    public void audit(String auditCode, EventType eventType, String initiator, AuditContextParams auditContextParams) {
        String eventId = UUID.randomUUID().toString();
        log.debug("Audit event [{}] type [{}] with event id [{}]", auditCode, eventType, eventId);
        try {
            AuditEventTemplateModel auditEventTemplate =
                    auditEventTemplateStore.getAuditEventTemplate(auditCode, eventType);
            if (!Boolean.TRUE.equals(auditEventTemplate.getAuditCode().isEnabled())) {
                log.warn("Audit code [{}] is disabled", auditCode);
            } else {
                String message = auditTemplateProcessorService.process(auditCode, eventType, auditContextParams);
                AuditEventRequest auditEventRequest = auditMapper.map(auditEventTemplate);
                auditEventRequest.setEventId(eventId);
                auditEventRequest.setMessage(message);
                auditEventRequest.setInitiator(initiator);
                auditEventRequest.setEventDate(LocalDateTime.now());
                log.info("Starting to send audit event [{}] with code [{}], type [{}]", auditEventRequest.getEventId(),
                        auditEventRequest.getCode(), auditEventRequest.getEventType());
                auditEventSender.sendEvent(auditEventRequest);
                log.info("Audit event [{}] with code [{}], type [{}] has been sent", auditEventRequest.getEventId(),
                        auditEventRequest.getCode(), auditEventRequest.getEventType());
            }
        } catch (Exception ex) {
            log.error("There was an error while sending audit event [{}]: {}", eventId, ex.getMessage());
        }
    }
}
