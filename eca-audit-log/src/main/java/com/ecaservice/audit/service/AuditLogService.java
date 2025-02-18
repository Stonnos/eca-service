package com.ecaservice.audit.service;

import com.ecaservice.audit.config.AuditLogProperties;
import com.ecaservice.audit.dto.AuditEventRequest;
import com.ecaservice.audit.entity.AuditLogEntity;
import com.ecaservice.audit.entity.AuditLogEntity_;
import com.ecaservice.audit.exception.DuplicateEventIdException;
import com.ecaservice.audit.filter.AuditLogFilter;
import com.ecaservice.audit.filter.customize.CorrelationIdFilterFieldCustomizer;
import com.ecaservice.audit.mapping.AuditLogMapper;
import com.ecaservice.audit.repository.AuditLogRepository;
import com.ecaservice.core.filter.service.FilterTemplateService;
import com.ecaservice.core.filter.specification.FilterFieldCustomizer;
import com.ecaservice.core.filter.specification.UuidFilterFieldCustomizer;
import com.ecaservice.core.filter.validation.annotations.ValidPageRequest;
import com.ecaservice.core.lock.annotation.Locked;
import com.ecaservice.web.dto.model.AuditLogDto;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static com.ecaservice.audit.dictionary.FilterDictionaries.AUDIT_LOG_TEMPLATE;
import static com.ecaservice.audit.entity.AuditLogEntity_.EVENT_DATE;
import static com.ecaservice.core.filter.util.FilterUtils.buildSort;
import static com.google.common.collect.Lists.newArrayList;

/**
 * Service to manage with audit logs.
 *
 * @author Roman Batygin
 */
@Slf4j
@Validated
@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogProperties auditLogProperties;
    private final AuditLogMapper auditLogMapper;
    private final FilterTemplateService filterTemplateService;
    private final AuditLogRepository auditLogRepository;

    private final List<FilterFieldCustomizer> globalFilterFieldCustomizers = newArrayList();

    /**
     * Initialization method.
     */
    @PostConstruct
    public void initialize() {
        globalFilterFieldCustomizers.add(new UuidFilterFieldCustomizer(AuditLogEntity_.EVENT_ID));
        globalFilterFieldCustomizers.add(new CorrelationIdFilterFieldCustomizer());
    }

    /**
     * Saves audit event into database.
     *
     * @param auditEventRequest - audit event request
     * @return audit log entity
     */
    @Locked(lockName = "saveAuditEvent", key = "#auditEventRequest.eventId")
    public AuditLogEntity save(AuditEventRequest auditEventRequest) {
        log.info("Starting to save audit event [{}], code [{}] type [{}]", auditEventRequest.getEventId(),
                auditEventRequest.getCode(), auditEventRequest.getEventType());
        if (auditLogRepository.existsByEventId(auditEventRequest.getEventId())) {
            throw new DuplicateEventIdException(auditEventRequest.getEventId());
        }
        AuditLogEntity auditLogEntity = auditLogMapper.map(auditEventRequest);
        auditLogRepository.save(auditLogEntity);
        log.info("Audit event [{}] with code [{}], type [{}] has been saved", auditEventRequest.getEventId(),
                auditEventRequest.getCode(), auditEventRequest.getEventType());
        return auditLogEntity;
    }

    /**
     * Gets audit logs page.
     *
     * @param pageRequestDto - page request dto
     * @return audit logs page
     */
    public Page<AuditLogEntity> getNextPage(
            @ValidPageRequest(filterTemplateName = AUDIT_LOG_TEMPLATE) PageRequestDto pageRequestDto) {
        log.info("Gets audit logs next page: {}", pageRequestDto);
        Sort sort = buildSort(pageRequestDto.getSortFields(), EVENT_DATE, true);
        List<String> globalFilterFields = filterTemplateService.getGlobalFilterFields(AUDIT_LOG_TEMPLATE);
        AuditLogFilter filter = new AuditLogFilter(pageRequestDto.getSearchQuery(), globalFilterFields,
                pageRequestDto.getFilters());
        filter.setGlobalFilterFieldsCustomizers(globalFilterFieldCustomizers);
        var pageRequest = PageRequest.of(pageRequestDto.getPage(), pageRequestDto.getSize(), sort);
        var auditLogsPage = auditLogRepository.findAll(filter, pageRequest);
        log.info("Audit logs page [{} of {}] with size [{}] has been fetched for page request [{}]",
                auditLogsPage.getNumber(), auditLogsPage.getTotalPages(), auditLogsPage.getNumberOfElements(),
                pageRequestDto);
        return auditLogsPage;
    }

    /**
     * Gets audit logs page.
     *
     * @param pageRequestDto - page request dto
     * @return audit logs page
     */
    public PageDto<AuditLogDto> getAuditLogsPage(
            @ValidPageRequest(filterTemplateName = AUDIT_LOG_TEMPLATE) PageRequestDto pageRequestDto) {
        var auditLogsPage = getNextPage(pageRequestDto);
        List<AuditLogDto> auditLogDtoList = auditLogMapper.map(auditLogsPage.getContent());
        long totalElements = Long.min(auditLogsPage.getTotalElements(),
                pageRequestDto.getSize() * auditLogProperties.getMaxPagesNum());
        return PageDto.of(auditLogDtoList, pageRequestDto.getPage(), totalElements);
    }
}
