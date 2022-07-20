package com.ecaservice.server.report;

import com.ecaservice.base.model.ExperimentType;
import com.ecaservice.core.filter.service.FilterService;
import com.ecaservice.report.model.BaseReportBean;
import com.ecaservice.report.model.ExperimentBean;
import com.ecaservice.s3.client.minio.service.ObjectStorageService;
import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.config.AppProperties;
import com.ecaservice.server.config.CrossValidationConfig;
import com.ecaservice.server.config.ExperimentConfig;
import com.ecaservice.server.mapping.DateTimeConverter;
import com.ecaservice.server.mapping.ExperimentMapper;
import com.ecaservice.server.mapping.ExperimentMapperImpl;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.Experiment_;
import com.ecaservice.server.repository.ExperimentRepository;
import com.ecaservice.server.service.AbstractJpaTest;
import com.ecaservice.server.service.evaluation.CalculationExecutorService;
import com.ecaservice.server.service.evaluation.CalculationExecutorServiceImpl;
import com.ecaservice.server.service.experiment.ExperimentProcessorService;
import com.ecaservice.server.service.experiment.ExperimentService;
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

import static com.ecaservice.server.AssertionUtils.assertBaseReportBean;
import static com.ecaservice.server.PageRequestUtils.PAGE_NUMBER;
import static com.ecaservice.server.PageRequestUtils.PAGE_SIZE;
import static com.google.common.collect.Lists.newArrayList;

/**
 * Unit tests that checks ExperimentsBaseReportDataFetcher functionality {@see ExperimentsBaseReportDataFetcher}.
 *
 * @author Roman Batygin
 */
@Import({ExperimentMapperImpl.class, ExperimentConfig.class, AppProperties.class, CrossValidationConfig.class,
        DateTimeConverter.class})
class ExperimentsBaseReportDataFetcherTest extends AbstractJpaTest {

    private static final List<String> DATE_RANGE_VALUES = ImmutableList.of("2018-01-01", "2018-01-07");
    private static final LocalDateTime CREATION_DATE = LocalDateTime.of(2018, 1, 5, 0, 0, 0);

    @Mock
    private ObjectStorageService objectStorageService;
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
    private AppProperties appProperties;
    @Inject
    private ExperimentRepository experimentRepository;

    private ExperimentsBaseReportDataFetcher experimentsBaseReportDataFetcher;

    @Override
    public void init() {
        CalculationExecutorService executorService =
                new CalculationExecutorServiceImpl(Executors.newCachedThreadPool());
        ExperimentService experimentService =
                new ExperimentService(experimentRepository, executorService, experimentMapper, objectStorageService,
                        crossValidationConfig, experimentConfig, experimentProcessorService, entityManager,
                        appProperties, filterService);
        experimentsBaseReportDataFetcher =
                new ExperimentsBaseReportDataFetcher(filterService, experimentService, experimentMapper);
    }

    @Override
    public void deleteAll() {
        experimentRepository.deleteAll();
    }

    @Test
    void testFetchExperimentsData() {
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
