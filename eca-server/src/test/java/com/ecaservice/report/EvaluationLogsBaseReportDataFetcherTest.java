package com.ecaservice.report;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.config.CommonConfig;
import com.ecaservice.mapping.ClassifierInfoMapperImpl;
import com.ecaservice.mapping.ClassifierInputOptionsMapperImpl;
import com.ecaservice.mapping.EvaluationLogMapper;
import com.ecaservice.mapping.EvaluationLogMapperImpl;
import com.ecaservice.mapping.InstancesInfoMapperImpl;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.entity.EvaluationLog_;
import com.ecaservice.model.entity.RequestStatus;
import com.ecaservice.report.model.BaseReportBean;
import com.ecaservice.report.model.EvaluationLogBean;
import com.ecaservice.repository.EvaluationLogRepository;
import com.ecaservice.repository.EvaluationResultsRequestEntityRepository;
import com.ecaservice.service.AbstractJpaTest;
import com.ecaservice.service.ers.ErsService;
import com.ecaservice.service.evaluation.EvaluationLogService;
import com.ecaservice.service.filter.FilterService;
import com.ecaservice.web.dto.model.FilterRequestDto;
import com.ecaservice.web.dto.model.MatchMode;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static com.ecaservice.AssertionUtils.assertBaseReportBean;
import static com.google.common.collect.Lists.newArrayList;

/**
 * Unit tests that checks EvaluationLogsBaseReportDataFetcher functionality {@see EvaluationLogsBaseReportDataFetcher}.
 *
 * @author Roman Batygin
 */
@Import({CommonConfig.class, ClassifierInfoMapperImpl.class, EvaluationLogMapperImpl.class,
        InstancesInfoMapperImpl.class, ClassifierInputOptionsMapperImpl.class})
public class EvaluationLogsBaseReportDataFetcherTest extends AbstractJpaTest {

    private static final int PAGE_NUMBER = 0;
    private static final int PAGE_SIZE = 10;
    private static final List<String> DATE_RANGE_VALUES = ImmutableList.of("2018-01-01", "2018-01-07");
    private static final LocalDateTime CREATION_DATE = LocalDateTime.of(2018, 1, 5, 0, 0, 0);

    @Mock
    private FilterService filterService;
    @Mock
    private ErsService ersService;

    @Inject
    private CommonConfig commonConfig;
    @Inject
    private EvaluationLogMapper evaluationLogMapper;
    @Inject
    private EvaluationLogRepository evaluationLogRepository;
    @Inject
    private EvaluationResultsRequestEntityRepository evaluationResultsRequestEntityRepository;

    private EvaluationLogsBaseReportDataFetcher evaluationLogsBaseReportDataFetcher;

    @Override
    public void init() {
        EvaluationLogService evaluationLogService =
                new EvaluationLogService(commonConfig, filterService, evaluationLogMapper, ersService,
                        evaluationLogRepository, evaluationResultsRequestEntityRepository);
        evaluationLogsBaseReportDataFetcher =
                new EvaluationLogsBaseReportDataFetcher(filterService, evaluationLogService, evaluationLogMapper);
    }

    @Override
    public void deleteAll() {
        evaluationLogRepository.deleteAll();
    }

    @Test
    void testFetchEvaluationLogsData() {
        EvaluationLog evaluationLog = TestHelperUtils.createEvaluationLog();
        evaluationLog.setCreationDate(CREATION_DATE);
        evaluationLog.setRequestStatus(RequestStatus.FINISHED);
        evaluationLogRepository.save(evaluationLog);
        String searchQuery = evaluationLog.getRequestId().substring(0, 10);
        PageRequestDto pageRequestDto =
                new PageRequestDto(PAGE_NUMBER, PAGE_SIZE, EvaluationLog_.CREATION_DATE, false, searchQuery,
                        newArrayList());
        pageRequestDto.getFilters().add(
                new FilterRequestDto(EvaluationLog_.CREATION_DATE, DATE_RANGE_VALUES, MatchMode.RANGE));
        pageRequestDto.getFilters().add(
                new FilterRequestDto(EvaluationLog_.REQUEST_ID, Collections.singletonList(evaluationLog.getRequestId()),
                        MatchMode.LIKE));
        pageRequestDto.getFilters().add(new FilterRequestDto(EvaluationLog_.REQUEST_STATUS,
                Collections.singletonList(RequestStatus.FINISHED.name()), MatchMode.EQUALS));
        BaseReportBean<EvaluationLogBean> baseReportBean =
                evaluationLogsBaseReportDataFetcher.fetchReportData(pageRequestDto);
        assertBaseReportBean(baseReportBean, pageRequestDto);
    }
}
