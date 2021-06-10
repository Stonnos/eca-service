package com.ecaservice.core.audit.service;

import com.ecaservice.core.audit.entity.EventType;
import com.ecaservice.core.audit.model.AuditContextParams;
import com.ecaservice.core.audit.service.template.AuditTemplateProcessorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
        var auditEventTemplate = auditEventTemplateStore.getAuditEventTemplate(auditCode, eventType);
        if (!Boolean.TRUE.equals(auditEventTemplate.getAuditCode().getEnabled())) {
            log.warn("Audit code [{}] is disabled", auditCode);
        } else {
            log.info("Audit event [{}] of type [{}]", auditCode, eventType);
            String message = auditTemplateProcessorService.process(auditCode, eventType, auditContextParams);
            log.info("Audit event [{}] message: [{}]", auditCode, message);
        }
    }
}
