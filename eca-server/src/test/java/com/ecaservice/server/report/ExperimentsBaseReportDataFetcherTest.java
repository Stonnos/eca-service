package com.ecaservice.server.report;

import com.ecaservice.base.model.ExperimentType;
import com.ecaservice.core.filter.service.FilterTemplateService;
import com.ecaservice.report.model.BaseReportBean;
import com.ecaservice.s3.client.minio.service.ObjectStorageService;
import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.config.AppProperties;
import com.ecaservice.server.config.CrossValidationConfig;
import com.ecaservice.server.config.ExperimentConfig;
import com.ecaservice.server.mapping.DateTimeConverter;
import com.ecaservice.server.mapping.ExperimentMapperImpl;
import com.ecaservice.server.mapping.InstancesInfoMapperImpl;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.Experiment_;
import com.ecaservice.server.report.model.ExperimentBean;
import com.ecaservice.server.repository.ExperimentRepository;
import com.ecaservice.server.repository.InstancesInfoRepository;
import com.ecaservice.server.service.AbstractJpaTest;
import com.ecaservice.server.service.experiment.ExperimentCountQueryExecutor;
import com.ecaservice.server.service.experiment.ExperimentDataService;
import com.ecaservice.server.service.experiment.ExperimentStepProcessor;
import com.ecaservice.web.dto.model.FilterRequestDto;
import com.ecaservice.web.dto.model.MatchMode;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.ecaservice.web.dto.model.SortFieldRequestDto;
import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

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
        DateTimeConverter.class, InstancesInfoMapperImpl.class, ExperimentDataService.class,
        ExperimentsBaseReportDataFetcher.class, ExperimentCountQueryExecutor.class})
class ExperimentsBaseReportDataFetcherTest extends AbstractJpaTest {

    private static final List<String> DATE_RANGE_VALUES = ImmutableList.of("2018-01-01", "2018-01-07");
    private static final LocalDateTime CREATION_DATE = LocalDateTime.of(2018, 1, 5, 0, 0, 0);

    @MockBean
    private ObjectStorageService objectStorageService;
    @MockBean
    private FilterTemplateService filterTemplateService;
    @MockBean
    private ExperimentStepProcessor experimentStepProcessor;

    @Autowired
    private ExperimentRepository experimentRepository;
    @Autowired
    private InstancesInfoRepository instancesInfoRepository;

    @Autowired
    private ExperimentsBaseReportDataFetcher experimentsBaseReportDataFetcher;

    @Override
    public void deleteAll() {
        experimentRepository.deleteAll();
        instancesInfoRepository.deleteAll();
    }

    @Test
    void testFetchExperimentsData() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        experiment.setCreationDate(CREATION_DATE);
        experiment.setExperimentType(ExperimentType.ADA_BOOST);
        instancesInfoRepository.save(experiment.getInstancesInfo());
        experimentRepository.save(experiment);
        String searchQuery = experiment.getRequestId().substring(0, 10);
        PageRequestDto pageRequestDto = new PageRequestDto(PAGE_NUMBER, PAGE_SIZE,
                Collections.singletonList(new SortFieldRequestDto(Experiment_.CREATION_DATE, false)), searchQuery,
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
        assertBaseReportBean(baseReportBean);
    }
}
