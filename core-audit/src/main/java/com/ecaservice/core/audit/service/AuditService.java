package com.ecaservice.core.audit.service;

import com.ecaservice.core.audit.entity.AuditCodeEntity;
import com.ecaservice.core.audit.exception.AuditEntityNotFoundException;
import com.ecaservice.core.audit.repository.AuditCodeRepository;
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
public class AuditService {

    private final AuditCodeRepository auditCodeRepository;

    /**
     * Gets audit code model.
     *
     * @param code - audit code
     * @return audit code entity
     */
    public AuditCodeEntity getAuditCode(String code) {
        log.debug("Gets audit code [{}]", code);
        return auditCodeRepository.findById(code)
                .orElseThrow(() -> new AuditEntityNotFoundException(String.format("Audit code [%s] not found", code)));
    }
}
