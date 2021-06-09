package com.ecaservice.core.audit.service;

import com.ecaservice.core.audit.entity.AuditCodeEntity;
import com.ecaservice.core.audit.entity.AuditEventTemplateEntity;
import com.ecaservice.core.audit.entity.EventType;
import com.ecaservice.core.audit.exception.AuditDataNotFoundException;
import com.ecaservice.core.audit.repository.AuditCodeRepository;
import com.ecaservice.core.audit.repository.AuditEventTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service to manage with audit codes.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditCodeService {

    private final AuditCodeRepository auditCodeRepository;
    private final AuditEventTemplateRepository auditEventTemplateRepository;

    /**
     * Gets audit code entity.
     *
     * @param code - code value
     * @return audit code entity
     */
    public AuditCodeEntity getAuditCode(String code) {
        log.debug("Gets audit code [{}]", code);
        return auditCodeRepository.findById(code)
                .orElseThrow(() -> new AuditDataNotFoundException(String.format("Audit code [%s] not found", code)));
    }

    /**
     * Gets audit event entity.
     *
     * @param auditCode - audit code
     * @param eventType - event type
     * @return audit event entity
     */
    public AuditEventTemplateEntity getAuditEvent(AuditCodeEntity auditCode, EventType eventType) {
        log.debug("Gets audit event with code [{}], type [{}]", auditCode.getId(), eventType);
        return auditEventTemplateRepository.findByAuditCodeAndEventType(auditCode, eventType)
                .orElseThrow(() -> new AuditDataNotFoundException(
                        String.format("Audit event with code [%s], type [%s] not found", auditCode.getId(),
                                eventType)));
    }
}
