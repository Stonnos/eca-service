package com.ecaservice.audit.mapping;

import com.ecaservice.audit.dto.AuditEventRequest;
import com.ecaservice.audit.entity.AuditLogEntity;
import org.mapstruct.Mapper;

/**
 * Audit log mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public interface AuditLogMapper {

    /**
     * Maps audit event request to audit log entity.
     *
     * @param auditEventRequest - audit event request
     * @return audit log entity
     */
    AuditLogEntity map(AuditEventRequest auditEventRequest);
}
