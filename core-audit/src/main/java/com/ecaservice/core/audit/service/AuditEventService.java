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

/**
 * Service to sending audit events.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditEventService {

    private final AuditEventTemplateStore auditEventTemplateStore;
    private final AuditTemplateProcessorService auditTemplateProcessorService;
    private final AuditEventInitiator auditEventInitiator;
    private final AuditMapper auditMapper;

    /**
     * Send audit event.
     *
     * @param eventId            - event correlation id
     * @param auditCode          - audit code
     * @param eventType          - event type
     * @param auditContextParams - audit context params
     */
    public void audit(String eventId, String auditCode, EventType eventType, AuditContextParams auditContextParams) {
        log.debug("Audit event [{}] type [{}] with correlation id [{}]", auditCode, eventType, eventId);
        AuditEventTemplateModel auditEventTemplate =
                auditEventTemplateStore.getAuditEventTemplate(auditCode, eventType);
        if (!Boolean.TRUE.equals(auditEventTemplate.getAuditCode().isEnabled())) {
            log.warn("Audit code [{}] is disabled", auditCode);
        } else {
            log.info("Audit event [{}] of type [{}]", auditCode, eventType);
            String message = auditTemplateProcessorService.process(auditCode, eventType, auditContextParams);
            String eventInitiator = auditEventInitiator.getInitiator();
            log.info("Audit event [{}] message: [{}], initiator [{}]", auditCode, message, eventInitiator);
            AuditEventRequest auditEventRequest = auditMapper.map(auditEventTemplate);
            auditEventRequest.setEventId(eventId);
            auditEventRequest.setMessage(message);
            auditEventRequest.setInitiator(eventInitiator);
            auditEventRequest.setEventDate(LocalDateTime.now());
            log.info("Audit event request: {}", auditEventRequest);
        }
    }
}
