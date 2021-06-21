package com.ecaservice.audit.report;

import com.ecaservice.audit.AbstractJpaTest;
import com.ecaservice.audit.config.EcaAuditLogConfig;
import com.ecaservice.audit.entity.AuditLogEntity_;
import com.ecaservice.audit.mapping.AuditLogMapperImpl;
import com.ecaservice.audit.repository.AuditLogRepository;
import com.ecaservice.audit.service.AuditLogService;
import com.ecaservice.core.filter.service.FilterService;
import com.ecaservice.web.dto.model.FilterRequestDto;
import com.ecaservice.web.dto.model.MatchMode;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.ecaservice.audit.TestHelperUtils.createAuditLog;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests that checks EvaluationLogsBaseReportDataFetcher functionality {@see AuditLogsBaseReportDataFetcher}.
 *
 * @author Roman Batygin
 */
@Import({AuditLogsBaseReportDataFetcher.class, AuditLogMapperImpl.class, AuditLogService.class,
        EcaAuditLogConfig.class})
class AuditLogsBaseReportDataFetcherTest extends AbstractJpaTest {

    private static final List<String> DATE_RANGE_VALUES = ImmutableList.of("2018-01-01", "2018-07-07");
    private static final String GROUP_1 = "GROUP1";
    private static final String GROUP_2 = "GROUP2";
    private static final String CODE_1 = "code1";
    private static final String CODE_2 = "code2";
    private static final String CODE_3 = "code3";
    private static final int PAGE_SIZE = 5;
    private static final int PAGE = 0;
    private static final int EXPECTED_SIZE = 2;
    private static final LocalDateTime FIRST_DATE = LocalDateTime.of(2018, 2, 2, 0, 0, 0);
    private static final LocalDateTime SECOND_DATE = LocalDateTime.of(2018, 3, 5, 0, 0, 0);
    private static final LocalDateTime THIRD_DATE = LocalDateTime.of(2019, 2, 2, 0, 0, 0);

    @MockBean
    private FilterService filterService;

    @Inject
    private AuditLogRepository auditLogRepository;

    @Inject
    private AuditLogsBaseReportDataFetcher auditLogsBaseReportDataFetcher;


    @Override
    public void deleteAll() {
        auditLogRepository.deleteAll();
    }

    @Test
    void testFetchAuditLogsData() {
        var auditLog = createAuditLog(GROUP_1, CODE_1);
        auditLog.setEventDate(FIRST_DATE);
        var auditLog1 = createAuditLog(GROUP_1, CODE_2);
        auditLog1.setEventDate(SECOND_DATE);
        var auditLog2 = createAuditLog(GROUP_2, CODE_3);
        auditLog2.setEventDate(THIRD_DATE);
        auditLogRepository.saveAll(Arrays.asList(auditLog, auditLog1, auditLog2));
        var pageRequestDto = new PageRequestDto(PAGE, PAGE_SIZE, AuditLogEntity_.EVENT_DATE,
                false, null, newArrayList());
        pageRequestDto.getFilters().add(
                new FilterRequestDto(AuditLogEntity_.EVENT_DATE, DATE_RANGE_VALUES, MatchMode.RANGE));
        pageRequestDto.getFilters().add(
                new FilterRequestDto(AuditLogEntity_.GROUP_CODE, Collections.singletonList(GROUP_1),
                        MatchMode.EQUALS));
        var baseReportBean = auditLogsBaseReportDataFetcher.fetchReportData(pageRequestDto);
        assertThat(baseReportBean).isNotNull();
        assertThat(baseReportBean.getPage()).isOne();
        assertThat(baseReportBean.getTotalPages()).isOne();
        assertThat(baseReportBean.getSearchQuery()).isNull();
        assertThat(baseReportBean.getItems()).isNotNull();
        assertThat(baseReportBean.getItems()).hasSize(EXPECTED_SIZE);
        assertThat(baseReportBean.getFilters()).hasSameSizeAs(pageRequestDto.getFilters());
    }
}
