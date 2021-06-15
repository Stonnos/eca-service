package com.ecaservice.audit.service;

import com.ecaservice.audit.dto.AuditEventRequest;
import com.ecaservice.audit.entity.AuditLogEntity;
import com.ecaservice.audit.mapping.AuditLogMapper;
import com.ecaservice.audit.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service to manage with audit logs.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogMapper auditLogMapper;
    private final AuditLogRepository auditLogRepository;

    /**
     * Saves audit event into database.
     *
     * @param auditEventRequest - audit event request
     * @return audit log entity
     */
    public AuditLogEntity save(AuditEventRequest auditEventRequest) {
        log.info("Starting to save audit event [{}], code [{}] type [{}]", auditEventRequest.getEventId(),
                auditEventRequest.getCode(), auditEventRequest.getEventType());
        var auditLogEntity = auditLogMapper.map(auditEventRequest);
        auditLogRepository.save(auditLogEntity);
        log.info("Audit event [{}] with code [{}], type [{}] has been saved", auditEventRequest.getEventId(),
                auditEventRequest.getCode(), auditEventRequest.getEventType());
        return auditLogEntity;
    }
}
