package com.ecaservice.service.report;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.config.CommonConfig;
import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.mapping.*;
import com.ecaservice.model.entity.*;
import com.ecaservice.model.experiment.ExperimentType;
import com.ecaservice.report.BaseReportDataFetcher;
import com.ecaservice.report.model.BaseReportBean;
import com.ecaservice.report.model.EvaluationLogBean;
import com.ecaservice.report.model.ExperimentBean;
import com.ecaservice.repository.EvaluationLogRepository;
import com.ecaservice.repository.EvaluationResultsRequestEntityRepository;
import com.ecaservice.repository.ExperimentRepository;
import com.ecaservice.service.AbstractJpaTest;
import com.ecaservice.service.ers.ErsService;
import com.ecaservice.service.evaluation.CalculationExecutorService;
import com.ecaservice.service.evaluation.CalculationExecutorServiceImpl;
import com.ecaservice.service.evaluation.EvaluationLogService;
import com.ecaservice.service.experiment.DataService;
import com.ecaservice.service.experiment.ExperimentProcessorService;
import com.ecaservice.service.experiment.ExperimentService;
import com.ecaservice.service.filter.FilterService;
import com.ecaservice.web.dto.model.FilterRequestDto;
import com.ecaservice.web.dto.model.MatchMode;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests that checks BaseReportDataFetcher functionality {@see BaseReportDataFetcher}.
 *
 * @author Roman Batygin
 */
@Import({ExperimentMapperImpl.class, ExperimentConfig.class, CommonConfig.class,
        ClassifierInfoMapperImpl.class, EvaluationLogMapperImpl.class,
        InstancesInfoMapperImpl.class, ClassifierInputOptionsMapperImpl.class})
public class BaseReportDataFetcherTest extends AbstractJpaTest {

    private static final int PAGE_NUMBER = 0;
    private static final int PAGE_SIZE = 10;
    private static final List<String> DATE_RANGE_VALUES = ImmutableList.of("2018-01-01", "2018-01-07");
    private static final LocalDateTime CREATION_DATE = LocalDateTime.of(2018, 1, 5, 0, 0, 0);

    @Mock
    private DataService dataService;
    @Mock
    private FilterService filterService;
    @Mock
    private ExperimentProcessorService experimentProcessorService;
    @Mock
    private ErsService ersService;

    @Inject
    private ExperimentMapper experimentMapper;
    @Inject
    private ExperimentConfig experimentConfig;
    @Inject
    private EntityManager entityManager;
    @Inject
    private CommonConfig commonConfig;
    @Inject
    private EvaluationLogMapper evaluationLogMapper;
    @Inject
    private EvaluationLogRepository evaluationLogRepository;
    @Inject
    private ExperimentRepository experimentRepository;
    @Inject
    private EvaluationResultsRequestEntityRepository evaluationResultsRequestEntityRepository;

    private BaseReportDataFetcher baseReportDataFetcher;

    @Override
    public void init() {
        CalculationExecutorService executorService =
                new CalculationExecutorServiceImpl(Executors.newCachedThreadPool());
        ExperimentService experimentService = new ExperimentService(experimentRepository, executorService, experimentMapper, dataService,
                experimentConfig, experimentProcessorService, entityManager, commonConfig, filterService);
        EvaluationLogService evaluationLogService = new EvaluationLogService(commonConfig, filterService, evaluationLogMapper, ersService,
                evaluationLogRepository, evaluationResultsRequestEntityRepository);
        baseReportDataFetcher = new BaseReportDataFetcher(filterService, experimentService, evaluationLogService, experimentMapper, evaluationLogMapper);
    }

    @Override
    public void deleteAll() {
        experimentRepository.deleteAll();
        evaluationLogRepository.deleteAll();
    }

    @Test
    public void testFetchExperimentsData() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        experiment.setCreationDate(CREATION_DATE);
        experiment.setExperimentType(ExperimentType.ADA_BOOST);
        experimentRepository.save(experiment);
        String searchQuery = experiment.getUuid().substring(0, 10);
        PageRequestDto pageRequestDto =
                new PageRequestDto(PAGE_NUMBER, PAGE_SIZE, Experiment_.CREATION_DATE, false, searchQuery, newArrayList());
        pageRequestDto.getFilters().add(
                new FilterRequestDto(Experiment_.CREATION_DATE, DATE_RANGE_VALUES,
                        MatchMode.RANGE));
        pageRequestDto.getFilters().add(
                new FilterRequestDto(Experiment_.UUID, Collections.singletonList(experiment.getUuid()),
                        MatchMode.LIKE));
        pageRequestDto.getFilters().add(
                new FilterRequestDto(Experiment_.EXPERIMENT_TYPE, Arrays.asList(ExperimentType.ADA_BOOST.name(), ExperimentType.HETEROGENEOUS_ENSEMBLE.name(), ExperimentType.NEURAL_NETWORKS.name()),
                        MatchMode.EQUALS));
        BaseReportBean<ExperimentBean> baseReportBean = baseReportDataFetcher.fetchExperimentsData(pageRequestDto);
        assertBaseReportBean(baseReportBean, pageRequestDto);
    }

    @Test
    public void testFetchEvaluationLogsData() {
        EvaluationLog evaluationLog = TestHelperUtils.createEvaluationLog();
        evaluationLog.setCreationDate(CREATION_DATE);
        evaluationLog.setEvaluationStatus(RequestStatus.FINISHED);
        evaluationLogRepository.save(evaluationLog);
        String searchQuery = evaluationLog.getRequestId().substring(0, 10);
        PageRequestDto pageRequestDto =
                new PageRequestDto(PAGE_NUMBER, PAGE_SIZE, EvaluationLog_.CREATION_DATE, false, searchQuery, newArrayList());
        pageRequestDto.getFilters().add(
                new FilterRequestDto(EvaluationLog_.CREATION_DATE, DATE_RANGE_VALUES,
                        MatchMode.RANGE));
        pageRequestDto.getFilters().add(
                new FilterRequestDto(EvaluationLog_.REQUEST_ID, Collections.singletonList(evaluationLog.getRequestId()),
                        MatchMode.LIKE));
        pageRequestDto.getFilters().add(
                new FilterRequestDto(EvaluationLog_.EVALUATION_STATUS, Collections.singletonList(RequestStatus.FINISHED.name()),
                        MatchMode.EQUALS));
        BaseReportBean<EvaluationLogBean> baseReportBean = baseReportDataFetcher.fetchEvaluationLogs(pageRequestDto);
        assertBaseReportBean(baseReportBean, pageRequestDto);
    }

    private <T> void assertBaseReportBean(BaseReportBean<T> baseReportBean, PageRequestDto pageRequestDto) {
        assertThat(baseReportBean).isNotNull();
        assertThat(baseReportBean.getPage()).isOne();
        assertThat(baseReportBean.getTotalPages()).isOne();
        assertThat(baseReportBean.getSearchQuery()).isNotNull();
        assertThat(baseReportBean.getItems()).isNotNull();
        assertThat(baseReportBean.getItems().size()).isOne();
        assertThat(baseReportBean.getFilters()).isNotNull();
        assertThat(baseReportBean.getFilters()).hasSameSizeAs(pageRequestDto.getFilters());
    }
}
