package com.ecaservice.audit.service;

import com.ecaservice.audit.AbstractJpaTest;
import com.ecaservice.audit.entity.AuditLogEntity;
import com.ecaservice.audit.exception.DuplicateEventIdException;
import com.ecaservice.audit.mapping.AuditLogMapperImpl;
import com.ecaservice.audit.repository.AuditLogRepository;
import com.ecaservice.core.filter.exception.FieldNotFoundException;
import com.ecaservice.core.filter.service.FilterService;
import com.ecaservice.web.dto.model.FilterRequestDto;
import com.ecaservice.web.dto.model.MatchMode;
import com.ecaservice.web.dto.model.PageRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

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
@Import({AuditLogMapperImpl.class, AuditLogService.class})
class AuditLogServiceTest extends AbstractJpaTest {

    private static final int PAGE_NUMBER = 0;
    private static final int PAGE_SIZE = 10;
    private static final int NUM_THREADS = 2;
    private static final String INVALID_FIELD_NAME = "abc.field1.field2";

    @Inject
    private AuditLogService auditLogService;
    @Inject
    private AuditLogRepository auditLogRepository;

    @MockBean
    private FilterService filterService;

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

    @Test
    void testDuplicateEventIdInMultiThreadEnvironment() throws Exception {
        var hasDuplicateEventIdError = new AtomicBoolean();
        var auditEventRequest = createAuditEventRequest();
        final CountDownLatch finishedLatch = new CountDownLatch(NUM_THREADS);
        ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);
        for (int i = 0; i < NUM_THREADS; i++) {
            executorService.submit(() -> {
                try {
                    auditLogService.save(auditEventRequest);
                } catch (DuplicateEventIdException ex) {
                    hasDuplicateEventIdError.set(true);
                } finally {
                    finishedLatch.countDown();
                }
            });
        }
        finishedLatch.await();
        executorService.shutdownNow();
        assertThat(hasDuplicateEventIdError.get()).isTrue();
        assertThat(auditLogRepository.count()).isOne();
    }

    /**
     * Tests global filtering by search query.
     */
    @Test
    void testGlobalFilter() {
        saveAuditLogs();
        PageRequestDto pageRequestDto =
                new PageRequestDto(PAGE_NUMBER, PAGE_SIZE, EVENT_DATE, false, "XGroup3", newArrayList());
        when(filterService.getGlobalFilterFields(AUDIT_LOG_TEMPLATE)).thenReturn(
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
        PageRequestDto pageRequestDto =
                new PageRequestDto(PAGE_NUMBER, PAGE_SIZE, EVENT_DATE, false, null, newArrayList());
        when(filterService.getGlobalFilterFields(AUDIT_LOG_TEMPLATE)).thenReturn(Collections.emptyList());
        pageRequestDto.getFilters().add(
                new FilterRequestDto(CODE, Collections.singletonList("Code2"), MatchMode.EQUALS));
        Page<AuditLogEntity> auditLogsPage = auditLogService.getNextPage(pageRequestDto);
        assertThat(auditLogsPage).isNotNull();
        assertThat(auditLogsPage.getTotalElements()).isOne();
    }

    @Test
    void testFilterShouldThrowFieldNotFoundException() {
        saveAuditLogs();
        PageRequestDto pageRequestDto =
                new PageRequestDto(PAGE_NUMBER, PAGE_SIZE, EVENT_DATE, false, null, newArrayList());
        when(filterService.getGlobalFilterFields(AUDIT_LOG_TEMPLATE)).thenReturn(Collections.emptyList());
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
