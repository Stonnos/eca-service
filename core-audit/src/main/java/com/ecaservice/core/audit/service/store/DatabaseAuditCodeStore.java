package com.ecaservice.core.audit.service.store;

import com.ecaservice.core.audit.entity.AuditCodeEntity;
import com.ecaservice.core.audit.entity.EventType;
import com.ecaservice.core.audit.exception.AuditDataNotFoundException;
import com.ecaservice.core.audit.mapping.AuditMapper;
import com.ecaservice.core.audit.model.AuditEventTemplateModel;
import com.ecaservice.core.audit.repository.AuditCodeRepository;
import com.ecaservice.core.audit.repository.AuditEventTemplateRepository;
import com.ecaservice.core.audit.service.AuditCodeStore;
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
public class DatabaseAuditCodeStore implements AuditCodeStore {

    private final AuditMapper auditMapper;
    private final AuditCodeRepository auditCodeRepository;
    private final AuditEventTemplateRepository auditEventTemplateRepository;

    @Override
    public AuditEventTemplateModel getAuditEventTemplate(String auditCode, EventType eventType) {
        log.debug("Gets audit event with code [{}], type [{}]", auditCode, eventType);
        var auditCodeEntity = getAuditCode(auditCode);
        var auditCodeTemplate = auditEventTemplateRepository
                .findByAuditCodeAndEventType(auditCodeEntity, eventType)
                .orElseThrow(() -> new AuditDataNotFoundException(
                        String.format("Audit event with code [%s], type [%s] not found", auditCode, eventType)));
        return auditMapper.map(auditCodeTemplate);
    }

    private AuditCodeEntity getAuditCode(String code) {
        return auditCodeRepository.findById(code)
                .orElseThrow(() -> new AuditDataNotFoundException(String.format("Audit code [%s] not found", code)));
    }
}
