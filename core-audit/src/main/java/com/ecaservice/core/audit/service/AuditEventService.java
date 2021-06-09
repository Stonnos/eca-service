package com.ecaservice.core.audit.service;

import com.ecaservice.core.audit.entity.EventType;
import com.ecaservice.core.audit.model.AuditCodeModel;
import com.ecaservice.core.audit.model.AuditEventTemplateModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Service to sending audit events.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditEventService {

    private final AuditCodeStore auditCodeStore;

    /**
     * Send audit event.
     *
     * @param eventId   - event correlation id
     * @param eventCode - event code
     * @param eventType - event type
     * @param params    - audited method params
     */
    public void audit(String eventId, String eventCode, EventType eventType, Map<String, Object> params) {
        log.debug("Audit event [{}] type [{}] with correlation id [{}]", eventCode, eventType, eventId);
        AuditCodeModel auditCode = auditCodeStore.getAuditCode(eventCode);
        if (!Boolean.TRUE.equals(auditCode.getEnabled())) {
            log.warn("Audit code [{}] is disabled", auditCode);
        } else {
            AuditEventTemplateModel auditEvent = auditCodeStore.getAuditEventTemplate(eventCode, eventType);
            log.info("Audit event [{}] of type [{}]", eventCode, eventType);
        }
    }
}
