package com.ecaservice.audit;

import com.ecaservice.audit.dto.AuditEventRequest;
import com.ecaservice.audit.dto.EventType;
import com.ecaservice.audit.entity.AuditLogEntity;
import com.ecaservice.report.model.BaseReportBean;
import com.ecaservice.web.dto.model.FilterFieldDto;
import com.ecaservice.web.dto.model.FilterFieldType;
import com.ecaservice.web.dto.model.MatchMode;
import com.ecaservice.web.dto.model.PageRequestDto;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

/**
 * Test helper utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class TestHelperUtils {

    private static final String INITIATOR = "user";
    private static final String CODE = "CODE";
    private static final String CODE_TITLE = "code title";
    private static final String GROUP = "GROUP";
    private static final String GROUP_TITLE = "group title";
    private static final String MESSAGE = "audit message";
    private static final String FILTER_NAME = "name";
    private static final String FILTER_DESCRIPTION = "description";

    private static final int PAGE_SIZE = 10;
    private static final int PAGE_NUMBER = 0;

    /**
     * Creates page request dto.
     *
     * @return page request dto
     */
    public static PageRequestDto createPageRequestDto() {
        return new PageRequestDto(PAGE_NUMBER, PAGE_SIZE, Collections.emptyList(), null, Collections.emptyList());
    }

    /**
     * Creates audit event request.
     *
     * @return audit event request
     */
    public static AuditEventRequest createAuditEventRequest() {
        var auditEventRequest = new AuditEventRequest();
        auditEventRequest.setEventId(UUID.randomUUID().toString());
        auditEventRequest.setCorrelationId(UUID.randomUUID().toString());
        auditEventRequest.setEventType(EventType.SUCCESS);
        auditEventRequest.setInitiator(INITIATOR);
        auditEventRequest.setCode(CODE);
        auditEventRequest.setCodeTitle(CODE_TITLE);
        auditEventRequest.setGroupCode(GROUP);
        auditEventRequest.setGroupTitle(GROUP_TITLE);
        auditEventRequest.setMessage(MESSAGE);
        auditEventRequest.setEventDate(LocalDateTime.now());
        return auditEventRequest;
    }

    /**
     * Creates audit event request.
     *
     * @param groupCode - group code
     * @param code      - audit code
     * @return audit event request
     */
    public static AuditLogEntity createAuditLog(String groupCode, String code) {
        var auditLogEntity = new AuditLogEntity();
        auditLogEntity.setEventId(UUID.randomUUID().toString());
        auditLogEntity.setEventType(EventType.SUCCESS);
        auditLogEntity.setInitiator(INITIATOR);
        auditLogEntity.setCode(code);
        auditLogEntity.setCodeTitle(CODE_TITLE);
        auditLogEntity.setGroupCode(groupCode);
        auditLogEntity.setGroupTitle(GROUP_TITLE);
        auditLogEntity.setMessage(MESSAGE);
        auditLogEntity.setEventDate(LocalDateTime.now());
        return auditLogEntity;
    }

    /**
     * Creates audit log entity.
     *
     * @return audit log entity
     */
    public static AuditLogEntity createAuditLog() {
        return createAuditLog(GROUP, CODE);
    }

    /**
     * Creates filter field dto.
     *
     * @return filter field dto
     */
    public static FilterFieldDto createFilterFieldDto() {
        FilterFieldDto filterField = new FilterFieldDto();
        filterField.setDescription(FILTER_DESCRIPTION);
        filterField.setFieldOrder(1);
        filterField.setFieldName(FILTER_NAME);
        filterField.setFilterFieldType(FilterFieldType.REFERENCE);
        filterField.setMatchMode(MatchMode.EQUALS);
        filterField.setMultiple(false);
        return filterField;
    }

    /**
     * Creates report bean.
     *
     * @param <T> - item generic type
     * @return base report bean
     */
    public static <T> BaseReportBean<T> createReportBean() {
        return BaseReportBean.<T>builder()
                .items(Collections.emptyList())
                .filters(Collections.emptyList())
                .build();
    }
}
