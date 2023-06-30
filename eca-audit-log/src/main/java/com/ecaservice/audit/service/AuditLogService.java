package com.ecaservice.audit.service;

import com.ecaservice.audit.dto.AuditEventRequest;
import com.ecaservice.audit.entity.AuditLogEntity;
import com.ecaservice.audit.exception.DuplicateEventIdException;
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
import java.util.concurrent.ConcurrentHashMap;

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

    private final AuditLogMapper auditLogMapper;
    private final FilterService filterService;
    private final AuditLogRepository auditLogRepository;

    private final ConcurrentHashMap<String, Object> eventIdsMap = new ConcurrentHashMap<>();

    /**
     * Saves audit event into database.
     *
     * @param auditEventRequest - audit event request
     * @return audit log entity
     */
    public AuditLogEntity save(AuditEventRequest auditEventRequest) {
        log.info("Starting to save audit event [{}], code [{}] type [{}]", auditEventRequest.getEventId(),
                auditEventRequest.getCode(), auditEventRequest.getEventType());
        AuditLogEntity auditLogEntity;
        eventIdsMap.putIfAbsent(auditEventRequest.getEventId(), new Object());
        synchronized (eventIdsMap.get(auditEventRequest.getEventId())) {
            if (auditLogRepository.existsByEventId(auditEventRequest.getEventId())) {
                throw new DuplicateEventIdException(auditEventRequest.getEventId());
            }
            auditLogEntity = auditLogMapper.map(auditEventRequest);
            auditLogRepository.save(auditLogEntity);
            log.info("Audit event [{}] with code [{}], type [{}] has been saved", auditEventRequest.getEventId(),
                    auditEventRequest.getCode(), auditEventRequest.getEventType());
        }
        eventIdsMap.remove(auditEventRequest.getEventId());
        return auditLogEntity;
    }

    /**
     * Gets audit logs page.
     *
     * @param pageRequestDto - page request dto
     * @return audit logs page
     */
    public Page<AuditLogEntity> getNextPage(PageRequestDto pageRequestDto) {
        log.info("Gets audit logs next page: {}", pageRequestDto);
        Sort sort = buildSort(pageRequestDto.getSortField(), EVENT_DATE, pageRequestDto.isAscending());
        List<String> globalFilterFields = filterService.getGlobalFilterFields(AUDIT_LOG_TEMPLATE);
        AuditLogFilter filter = new AuditLogFilter(pageRequestDto.getSearchQuery(), globalFilterFields,
                pageRequestDto.getFilters());
        var pageRequest = PageRequest.of(pageRequestDto.getPage(), pageRequestDto.getSize(), sort);
        var auditLogsPage = auditLogRepository.findAll(filter, pageRequest);
        log.info("Audit logs page [{} of {}] with size [{}] has been fetched for page request [{}]",
                auditLogsPage.getNumber(), auditLogsPage.getTotalPages(), auditLogsPage.getNumberOfElements(),
                pageRequestDto);
        return auditLogsPage;
    }
}
