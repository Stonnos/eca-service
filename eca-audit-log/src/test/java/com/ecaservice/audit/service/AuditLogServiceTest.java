package com.ecaservice.audit.service;

import com.ecaservice.audit.AbstractJpaTest;
import com.ecaservice.audit.config.AuditLogProperties;
import com.ecaservice.audit.entity.AuditLogEntity;
import com.ecaservice.audit.exception.DuplicateEventIdException;
import com.ecaservice.audit.mapping.AuditLogMapperImpl;
import com.ecaservice.audit.repository.AuditLogRepository;
import com.ecaservice.core.filter.exception.FieldNotFoundException;
import com.ecaservice.core.filter.service.FilterTemplateService;
import com.ecaservice.core.lock.config.CoreLockAutoConfiguration;
import com.ecaservice.core.lock.metrics.LockMeterService;
import com.ecaservice.web.dto.model.FilterRequestDto;
import com.ecaservice.web.dto.model.MatchMode;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.ecaservice.web.dto.model.SortFieldRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;

import java.util.Arrays;
import java.util.Collections;

import static com.ecaservice.audit.TestHelperUtils.createAuditEventRequest;
import static com.ecaservice.audit.TestHelperUtils.createAuditLog;
import static com.ecaservice.audit.dictionary.FilterDictionaries.AUDIT_LOG_TEMPLATE;
import static com.ecaservice.audit.entity.AuditLogEntity_.CODE;
import static com.ecaservice.audit.entity.AuditLogEntity_.EVENT_DATE;
import static com.ecaservice.audit.entity.AuditLogEntity_.EVENT_ID;
import static com.ecaservice.audit.entity.AuditLogEntity_.GROUP_CODE;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link AuditLogService} class.
 *
 * @author Roman Batygin
 */
@EnableAspectJAutoProxy
@Import({AuditLogMapperImpl.class, AuditLogService.class, CoreLockAutoConfiguration.class, AuditLogProperties.class})
class AuditLogServiceTest extends AbstractJpaTest {

    private static final int PAGE_NUMBER = 0;
    private static final int PAGE_SIZE = 10;
    private static final String INVALID_FIELD_NAME = "abc.field1.field2";

    @Autowired
    private AuditLogService auditLogService;
    @Autowired
    private AuditLogRepository auditLogRepository;

    @MockBean
    private FilterTemplateService filterTemplateService;
    @MockBean
    private LockMeterService lockMeterService;

    @Override
    public void deleteAll() {
        auditLogRepository.deleteAll();
    }

    @Test
    void testSaveAuditLog() {
        var auditEventRequest = createAuditEventRequest();
        var auditLog = auditLogService.save(auditEventRequest);
        assertThat(auditLog).isNotNull();
        var actual = auditLogRepository.findById(auditLog.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getEventId()).isEqualTo(auditEventRequest.getEventId());
        assertThat(actual.getCorrelationId()).isEqualTo(auditEventRequest.getCorrelationId());
    }

    @Test
    void testSaveAuditLogShouldThrowDuplicateEventIdException() {
        var auditEventRequest = createAuditEventRequest();
        var auditLog = auditLogService.save(auditEventRequest);
        assertThat(auditLog).isNotNull();
        assertThrows(DuplicateEventIdException.class, () -> auditLogService.save(auditEventRequest));
    }

    /**
     * Tests global filtering by search query.
     */
    @Test
    void testGlobalFilter() {
        saveAuditLogs();
        PageRequestDto pageRequestDto = new PageRequestDto(PAGE_NUMBER, PAGE_SIZE,
                Collections.singletonList(new SortFieldRequestDto(EVENT_DATE, false)), "XGroup3", newArrayList());
        when(filterTemplateService.getGlobalFilterFields(AUDIT_LOG_TEMPLATE)).thenReturn(
                Arrays.asList(EVENT_ID, GROUP_CODE, CODE));
        Page<AuditLogEntity> auditLogsPage = auditLogService.getNextPage(pageRequestDto);
        assertThat(auditLogsPage).isNotNull();
        assertThat(auditLogsPage.getTotalElements()).isOne();
    }

    /**
     * Tests filtering by audit code.
     */
    @Test
    void testFilterByAuditCode() {
        saveAuditLogs();
        PageRequestDto pageRequestDto = new PageRequestDto(PAGE_NUMBER, PAGE_SIZE, Collections.singletonList(new SortFieldRequestDto(EVENT_DATE, false)), null, newArrayList());
        when(filterTemplateService.getGlobalFilterFields(AUDIT_LOG_TEMPLATE)).thenReturn(Collections.emptyList());
        pageRequestDto.getFilters().add(
                new FilterRequestDto(CODE, Collections.singletonList("Code2"), MatchMode.EQUALS));
        Page<AuditLogEntity> auditLogsPage = auditLogService.getNextPage(pageRequestDto);
        assertThat(auditLogsPage).isNotNull();
        assertThat(auditLogsPage.getTotalElements()).isOne();
    }

    @Test
    void testFilterShouldThrowFieldNotFoundException() {
        saveAuditLogs();
        PageRequestDto pageRequestDto = new PageRequestDto(PAGE_NUMBER, PAGE_SIZE, Collections.singletonList(new SortFieldRequestDto(EVENT_DATE, false)), null, newArrayList());
        when(filterTemplateService.getGlobalFilterFields(AUDIT_LOG_TEMPLATE)).thenReturn(Collections.emptyList());
        pageRequestDto.getFilters().add(
                new FilterRequestDto(INVALID_FIELD_NAME, Collections.singletonList("Value"), MatchMode.EQUALS));
        assertThrows(FieldNotFoundException.class, () -> auditLogService.getNextPage(pageRequestDto));
    }

    private void saveAuditLogs() {
        var first = createAuditLog("Group1", "Code1");
        var second = createAuditLog("Group2", "Code2");
        var third = createAuditLog("XGroup3", "Code3");
        auditLogRepository.saveAll(Arrays.asList(first, second, third));
    }
}
