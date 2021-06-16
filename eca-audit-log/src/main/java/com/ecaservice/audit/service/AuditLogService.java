package com.ecaservice.audit.service;

import com.ecaservice.audit.config.EcaAuditLogConfig;
import com.ecaservice.audit.dto.AuditEventRequest;
import com.ecaservice.audit.entity.AuditLogEntity;
import com.ecaservice.audit.filter.AuditLogFilter;
import com.ecaservice.audit.mapping.AuditLogMapper;
import com.ecaservice.audit.repository.AuditLogRepository;
import com.ecaservice.core.filter.service.FilterService;
import com.ecaservice.web.dto.model.PageRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.ecaservice.audit.dictionary.FilterDictionaries.AUDIT_LOG_TEMPLATE;
import static com.ecaservice.audit.entity.AuditLogEntity_.EVENT_DATE;
import static com.ecaservice.core.filter.util.FilterUtils.buildSort;

/**
 * Service to manage with audit logs.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final EcaAuditLogConfig ecaAuditLogConfig;
    private final AuditLogMapper auditLogMapper;
    private final FilterService filterService;
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

    public Page<AuditLogEntity> getNextPage(PageRequestDto pageRequestDto) {
        Sort sort = buildSort(pageRequestDto.getSortField(), EVENT_DATE, pageRequestDto.isAscending());
        List<String> globalFilterFields = filterService.getGlobalFilterFields(AUDIT_LOG_TEMPLATE);
        AuditLogFilter filter = new AuditLogFilter(pageRequestDto.getSearchQuery(), globalFilterFields,
                pageRequestDto.getFilters());
        int pageSize = Integer.min(pageRequestDto.getSize(), ecaAuditLogConfig.getMaxPageSize());
        return auditLogRepository.findAll(filter, PageRequest.of(pageRequestDto.getPage(), pageSize, sort));
    }
}
