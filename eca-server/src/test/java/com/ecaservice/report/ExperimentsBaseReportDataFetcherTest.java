package com.ecaservice.report;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.config.CommonConfig;
import com.ecaservice.config.CrossValidationConfig;
import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.mapping.ExperimentMapper;
import com.ecaservice.mapping.ExperimentMapperImpl;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.Experiment_;
import com.ecaservice.model.experiment.ExperimentType;
import com.ecaservice.report.model.BaseReportBean;
import com.ecaservice.report.model.ExperimentBean;
import com.ecaservice.repository.ExperimentRepository;
import com.ecaservice.service.AbstractJpaTest;
import com.ecaservice.service.evaluation.CalculationExecutorService;
import com.ecaservice.service.evaluation.CalculationExecutorServiceImpl;
import com.ecaservice.service.experiment.DataService;
import com.ecaservice.service.experiment.ExperimentProcessorService;
import com.ecaservice.service.experiment.ExperimentService;
import com.ecaservice.service.filter.FilterService;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;

import static com.ecaservice.AssertionUtils.assertBaseReportBean;
import static com.google.common.collect.Lists.newArrayList;

/**
 * Unit tests that checks ExperimentsBaseReportDataFetcher functionality {@see ExperimentsBaseReportDataFetcher}.
 *
 * @author Roman Batygin
 */
@Import({ExperimentMapperImpl.class, ExperimentConfig.class, CommonConfig.class, CrossValidationConfig.class})
public class ExperimentsBaseReportDataFetcherTest extends AbstractJpaTest {

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

    @Inject
    private ExperimentMapper experimentMapper;
    @Inject
    private CrossValidationConfig crossValidationConfig;
    @Inject
    private ExperimentConfig experimentConfig;
    @Inject
    private EntityManager entityManager;
    @Inject
    private CommonConfig commonConfig;
    @Inject
    private ExperimentRepository experimentRepository;

    private ExperimentsBaseReportDataFetcher experimentsBaseReportDataFetcher;

    @Override
    public void init() {
        CalculationExecutorService executorService =
                new CalculationExecutorServiceImpl(Executors.newCachedThreadPool());
        ExperimentService experimentService =
                new ExperimentService(experimentRepository, executorService, experimentMapper, dataService,
                        crossValidationConfig, experimentConfig, experimentProcessorService, entityManager,
                        commonConfig, filterService);
        experimentsBaseReportDataFetcher =
                new ExperimentsBaseReportDataFetcher(filterService, experimentService, experimentMapper);
    }

    @Override
    public void deleteAll() {
        experimentRepository.deleteAll();
    }

    @Test
    public void testFetchExperimentsData() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        experiment.setCreationDate(CREATION_DATE);
        experiment.setExperimentType(ExperimentType.ADA_BOOST);
        experimentRepository.save(experiment);
        String searchQuery = experiment.getRequestId().substring(0, 10);
        PageRequestDto pageRequestDto =
                new PageRequestDto(PAGE_NUMBER, PAGE_SIZE, Experiment_.CREATION_DATE, false, searchQuery,
                        newArrayList());
        pageRequestDto.getFilters().add(
                new FilterRequestDto(Experiment_.CREATION_DATE, DATE_RANGE_VALUES, MatchMode.RANGE));
        pageRequestDto.getFilters().add(
                new FilterRequestDto(Experiment_.REQUEST_ID, Collections.singletonList(experiment.getRequestId()),
                        MatchMode.LIKE));
        pageRequestDto.getFilters().add(new FilterRequestDto(Experiment_.EXPERIMENT_TYPE,
                Arrays.asList(ExperimentType.ADA_BOOST.name(), ExperimentType.HETEROGENEOUS_ENSEMBLE.name(),
                        ExperimentType.NEURAL_NETWORKS.name()), MatchMode.EQUALS));
        BaseReportBean<ExperimentBean> baseReportBean =
                experimentsBaseReportDataFetcher.fetchReportData(pageRequestDto);
        assertBaseReportBean(baseReportBean, pageRequestDto);
    }
}
