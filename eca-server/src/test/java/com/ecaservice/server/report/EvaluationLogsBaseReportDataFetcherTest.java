package com.ecaservice.server.report;

import com.ecaservice.core.filter.service.FilterService;
import com.ecaservice.report.model.BaseReportBean;
import com.ecaservice.server.report.model.EvaluationLogBean;
import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.config.AppProperties;
import com.ecaservice.server.mapping.ClassifierInfoMapperImpl;
import com.ecaservice.server.mapping.DateTimeConverter;
import com.ecaservice.server.mapping.EvaluationLogMapper;
import com.ecaservice.server.mapping.EvaluationLogMapperImpl;
import com.ecaservice.server.mapping.InstancesInfoMapperImpl;
import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.server.model.entity.EvaluationLog_;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.repository.EvaluationLogRepository;
import com.ecaservice.server.repository.EvaluationResultsRequestEntityRepository;
import com.ecaservice.server.service.AbstractJpaTest;
import com.ecaservice.server.service.classifiers.ClassifierOptionsProcessor;
import com.ecaservice.server.service.ers.ErsService;
import com.ecaservice.server.service.evaluation.EvaluationLogService;
import com.ecaservice.web.dto.model.FilterRequestDto;
import com.ecaservice.web.dto.model.MatchMode;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static com.ecaservice.server.AssertionUtils.assertBaseReportBean;
import static com.ecaservice.server.PageRequestUtils.PAGE_NUMBER;
import static com.ecaservice.server.PageRequestUtils.PAGE_SIZE;
import static com.ecaservice.server.TestHelperUtils.createFilterDictionaryDto;
import static com.ecaservice.server.service.filter.dictionary.FilterDictionaries.CLASSIFIER_NAME;
import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Mockito.when;

/**
 * Unit tests that checks EvaluationLogsBaseReportDataFetcher functionality {@see EvaluationLogsBaseReportDataFetcher}.
 *
 * @author Roman Batygin
 */
@Import({AppProperties.class, ClassifierInfoMapperImpl.class, EvaluationLogMapperImpl.class,
        InstancesInfoMapperImpl.class, DateTimeConverter.class})
class EvaluationLogsBaseReportDataFetcherTest extends AbstractJpaTest {

    private static final List<String> DATE_RANGE_VALUES = ImmutableList.of("2018-01-01", "2018-01-07");
    private static final LocalDateTime CREATION_DATE = LocalDateTime.of(2018, 1, 5, 0, 0, 0);

    @Mock
    private FilterService filterService;
    @Mock
    private ErsService ersService;
    @Mock
    private ClassifierOptionsProcessor classifierOptionsProcessor;

    @Inject
    private AppProperties appProperties;
    @Inject
    private EvaluationLogMapper evaluationLogMapper;
    @Inject
    private EntityManager entityManager;
    @Inject
    private EvaluationLogRepository evaluationLogRepository;
    @Inject
    private EvaluationResultsRequestEntityRepository evaluationResultsRequestEntityRepository;

    private EvaluationLogsBaseReportDataFetcher evaluationLogsBaseReportDataFetcher;

    @Override
    public void init() {
        EvaluationLogService evaluationLogService =
                new EvaluationLogService(appProperties, filterService, evaluationLogMapper, classifierOptionsProcessor,
                        ersService, entityManager, evaluationLogRepository, evaluationResultsRequestEntityRepository);
        evaluationLogsBaseReportDataFetcher =
                new EvaluationLogsBaseReportDataFetcher(filterService, evaluationLogService, evaluationLogMapper);
        when(filterService.getFilterDictionary(CLASSIFIER_NAME)).thenReturn(createFilterDictionaryDto());
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
