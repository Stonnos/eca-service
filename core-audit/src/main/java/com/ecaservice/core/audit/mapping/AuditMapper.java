package com.ecaservice.core.audit.mapping;

import com.ecaservice.audit.dto.AuditEventRequest;
import com.ecaservice.core.audit.entity.AuditCodeEntity;
import com.ecaservice.core.audit.entity.AuditEventRequestEntity;
import com.ecaservice.core.audit.entity.AuditEventTemplateEntity;
import com.ecaservice.core.audit.entity.AuditGroupEntity;
import com.ecaservice.core.audit.model.AuditCodeModel;
import com.ecaservice.core.audit.model.AuditEventTemplateModel;
import com.ecaservice.core.audit.model.AuditGroupModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Audit mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public interface AuditMapper {

    /**
     * Maps audit group entity to audit group model.
     *
     * @param auditGroupEntity - audit group entity
     * @return audit group model
     */
    @Mapping(source = "id", target = "groupCode")
    AuditGroupModel map(AuditGroupEntity auditGroupEntity);

    /**
     * Maps audit code entity to audit code model.
     *
     * @param auditCodeEntity - audit code entity
     * @return audit code model
     */
    @Mapping(source = "id", target = "code")
    AuditCodeModel map(AuditCodeEntity auditCodeEntity);

    /**
     * Maps audit event template entity to audit event template model.
     *
     * @param auditEventTemplateEntity - audit template entity
     * @return audit event template model
     */
    AuditEventTemplateModel map(AuditEventTemplateEntity auditEventTemplateEntity);

    /**
     * Maps audit event template model to audit event request.
     *
     * @param auditEventTemplateModel - audit event template model
     * @return audit event request
     */
    @Mapping(source = "auditCode.code", target = "code")
    @Mapping(source = "auditCode.title", target = "codeTitle")
    @Mapping(source = "auditCode.auditGroup.groupCode", target = "groupCode")
    @Mapping(source = "auditCode.auditGroup.title", target = "groupTitle")
    AuditEventRequest map(AuditEventTemplateModel auditEventTemplateModel);

    /**
     * Maps audit event request to entity model.
     *
     * @param auditEventRequest - audit event request
     * @return audit event request entity
     */
    AuditEventRequestEntity map(AuditEventRequest auditEventRequest);
}
