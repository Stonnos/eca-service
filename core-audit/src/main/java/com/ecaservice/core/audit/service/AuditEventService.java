package com.ecaservice.core.audit.service;

import com.ecaservice.core.audit.entity.EventType;
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

    private final AuditEventTemplateStore auditEventTemplateStore;

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
        var auditEventTemplate = auditEventTemplateStore.getAuditEventTemplate(eventCode, eventType);
        if (!Boolean.TRUE.equals(auditEventTemplate.getAuditCode().getEnabled())) {
            log.warn("Audit code [{}] is disabled", eventCode);
        } else {
            log.info("Audit event [{}] of type [{}]", eventCode, eventType);
        }
    }
}
