package com.ecaservice.audit.mapping;

import com.ecaservice.audit.dto.AuditEventRequest;
import com.ecaservice.audit.entity.AuditLogEntity;
import com.ecaservice.audit.report.model.AuditLogBean;
import com.ecaservice.web.dto.model.AuditLogDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Audit log mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public abstract class AuditLogMapper {

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    /**
     * Maps audit event request to audit log entity.
     *
     * @param auditEventRequest - audit event request
     * @return audit log entity
     */
    public abstract AuditLogEntity map(AuditEventRequest auditEventRequest);

    /**
     * Maps audit log entity to dto model.
     *
     * @param auditLogEntity - audit log entity
     * @return audit log dto
     */
    public abstract AuditLogDto map(AuditLogEntity auditLogEntity);

    /**
     * Maps audit logs entities to dto models list.
     *
     * @param auditLogs - audit logs entities list
     * @return audit logs dto list
     */
    public abstract List<AuditLogDto> map(List<AuditLogEntity> auditLogs);

    /**
     * Maps audit log entity to audit log bean.
     *
     * @param auditLogEntity - audit log entity
     * @return audit log bean
     */
    @Mapping(source = "eventDate", target = "eventDate", qualifiedByName = "formatLocalDateTime")
    public abstract AuditLogBean mapToBean(AuditLogEntity auditLogEntity);

    /**
     * Maps audit logs entities to audit log beans list.
     *
     * @param auditLogs - audit logs entities list
     * @return audit log beans list
     */
    public abstract List<AuditLogBean> mapToBeans(List<AuditLogEntity> auditLogs);

    /**
     * Converts local date time to string in format yyy-MM-dd HH:mm:ss.
     *
     * @param localDateTime - local date time
     * @return date time string
     */
    @Named("formatLocalDateTime")
    String formatLocalDateTime(LocalDateTime localDateTime) {
        return Optional.ofNullable(localDateTime).map(dateTimeFormatter::format).orElse(null);
    }
}
