package com.ecaservice.server.report;

import com.ecaservice.core.filter.service.FilterTemplateService;
import com.ecaservice.report.model.BaseReportBean;
import com.ecaservice.s3.client.minio.service.ObjectStorageService;
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
import com.ecaservice.server.report.model.EvaluationLogBean;
import com.ecaservice.server.repository.EvaluationLogRepository;
import com.ecaservice.server.repository.EvaluationResultsRequestEntityRepository;
import com.ecaservice.server.repository.InstancesInfoRepository;
import com.ecaservice.server.service.AbstractJpaTest;
import com.ecaservice.server.service.classifiers.ClassifierOptionsInfoProcessor;
import com.ecaservice.server.service.ers.ErsService;
import com.ecaservice.server.service.evaluation.EvaluationLogDataService;
import com.ecaservice.web.dto.model.FilterRequestDto;
import com.ecaservice.web.dto.model.MatchMode;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.ecaservice.web.dto.model.SortFieldRequestDto;
import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import jakarta.persistence.EntityManager;
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
    private FilterTemplateService filterTemplateService;
    @Mock
    private ErsService ersService;
    @Mock
    private ClassifierOptionsInfoProcessor classifierOptionsInfoProcessor;
    @Mock
    private ObjectStorageService objectStorageService;

    @Autowired
    private AppProperties appProperties;
    @Autowired
    private EvaluationLogMapper evaluationLogMapper;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private EvaluationLogRepository evaluationLogRepository;
    @Autowired
    private InstancesInfoRepository instancesInfoRepository;
    @Autowired
    private EvaluationResultsRequestEntityRepository evaluationResultsRequestEntityRepository;

    private EvaluationLogsBaseReportDataFetcher evaluationLogsBaseReportDataFetcher;

    @Override
    public void init() {
        EvaluationLogDataService evaluationLogDataService =
                new EvaluationLogDataService(appProperties, filterTemplateService, evaluationLogMapper,
                        classifierOptionsInfoProcessor,
                        ersService, entityManager, objectStorageService, evaluationLogRepository,
                        evaluationResultsRequestEntityRepository);
        evaluationLogsBaseReportDataFetcher =
                new EvaluationLogsBaseReportDataFetcher(filterTemplateService, instancesInfoRepository,
                        evaluationLogDataService,
                        evaluationLogMapper);
        when(filterTemplateService.getFilterDictionary(CLASSIFIER_NAME)).thenReturn(createFilterDictionaryDto());
    }

    @Override
    public void deleteAll() {
        evaluationLogRepository.deleteAll();
        instancesInfoRepository.deleteAll();
    }

    @Test
    void testFetchEvaluationLogsData() {
        EvaluationLog evaluationLog = TestHelperUtils.createEvaluationLog();
        evaluationLog.setCreationDate(CREATION_DATE);
        evaluationLog.setRequestStatus(RequestStatus.FINISHED);
        instancesInfoRepository.save(evaluationLog.getInstancesInfo());
        evaluationLogRepository.save(evaluationLog);
        String searchQuery = evaluationLog.getRequestId().substring(0, 10);
        PageRequestDto pageRequestDto = new PageRequestDto(PAGE_NUMBER, PAGE_SIZE,
                Collections.singletonList(new SortFieldRequestDto(EvaluationLog_.CREATION_DATE, false)), searchQuery,
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
        assertBaseReportBean(baseReportBean);
    }
}
