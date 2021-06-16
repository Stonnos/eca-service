package com.ecaservice.audit.mapping;

import com.ecaservice.audit.dto.AuditEventRequest;
import com.ecaservice.audit.entity.AuditLogEntity;
import com.ecaservice.web.dto.model.AuditLogDto;
import org.mapstruct.Mapper;

import java.util.List;

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

    /**
     * Maps audit log entity to dto model.
     *
     * @param auditLogEntity - audit log entity
     * @return audit log dto
     */
    AuditLogDto map(AuditLogEntity auditLogEntity);

    /**
     * Maps audit logs entities to dto models list.
     *
     * @param auditLogs - audit logs entities list
     * @return audit logs dto list
     */
    List<AuditLogDto> map(List<AuditLogEntity> auditLogs);
}
