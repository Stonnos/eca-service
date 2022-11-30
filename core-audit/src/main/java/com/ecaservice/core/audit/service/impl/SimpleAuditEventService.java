package com.ecaservice.core.audit.service.impl;

import com.ecaservice.audit.dto.AuditEventRequest;
import com.ecaservice.core.audit.event.AuditEvent;
import com.ecaservice.core.audit.mapping.AuditMapper;
import com.ecaservice.core.audit.model.AuditEventTemplateModel;
import com.ecaservice.core.audit.service.AuditEventSender;
import com.ecaservice.core.audit.service.AuditEventService;
import com.ecaservice.core.audit.service.AuditEventTemplateStore;
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
public class SimpleAuditEventService implements AuditEventService {

    private final AuditEventTemplateStore auditEventTemplateStore;
    private final AuditTemplateProcessorService auditTemplateProcessorService;
    private final AuditMapper auditMapper;
    private final AuditEventSender auditEventSender;

    @Override
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
                auditEventRequest.setCorrelationId(auditEvent.getCorrelationId());
                auditEventRequest.setMessage(message);
                auditEventRequest.setInitiator(auditEvent.getInitiator());
                auditEventRequest.setEventDate(LocalDateTime.now());
                auditEventSender.sendAuditEvent(auditEventRequest);
            }
        } catch (Exception ex) {
            log.error("There was an error while process audit event [{}]: {}", eventId, ex.getMessage());
        }
    }
}
