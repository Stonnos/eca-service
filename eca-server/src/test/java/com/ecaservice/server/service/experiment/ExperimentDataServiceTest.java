package com.ecaservice.server.service.experiment;

import com.ecaservice.base.model.ExperimentType;
import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.core.filter.service.FilterService;
import com.ecaservice.s3.client.minio.model.GetPresignedUrlObject;
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
import com.ecaservice.server.model.entity.FilterTemplateType;
import com.ecaservice.server.model.entity.InstancesInfo;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.repository.ExperimentProgressRepository;
import com.ecaservice.server.repository.ExperimentRepository;
import com.ecaservice.server.repository.ExperimentStepRepository;
import com.ecaservice.server.repository.InstancesInfoRepository;
import com.ecaservice.server.service.AbstractJpaTest;
import com.ecaservice.web.dto.model.ChartDataDto;
import com.ecaservice.web.dto.model.FilterDictionaryDto;
import com.ecaservice.web.dto.model.FilterDictionaryValueDto;
import com.ecaservice.web.dto.model.FilterRequestDto;
import com.ecaservice.web.dto.model.MatchMode;
import com.ecaservice.web.dto.model.PageRequestDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static com.ecaservice.server.service.filter.dictionary.FilterDictionaries.EXPERIMENT_TYPE;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


/**
 * Unit tests that checks ExperimentService functionality {@see ExperimentService}.
 *
 * @author Roman Batygin
 */
@Import({ExperimentMapperImpl.class, ExperimentConfig.class, AppProperties.class, CrossValidationConfig.class,
        DateTimeConverter.class, InstancesInfoMapperImpl.class, ExperimentDataService.class,
        ExperimentProgressService.class})
class ExperimentDataServiceTest extends AbstractJpaTest {

    private static final int PAGE_NUMBER = 0;
    private static final int PAGE_SIZE = 10;
    private static final long INVALID_ID = 1000L;
    private static final String EXPERIMENT_DOWNLOAD_URL = "http://localhost:9000/experiment";

    @Inject
    private ExperimentRepository experimentRepository;
    @Inject
    private InstancesInfoRepository instancesInfoRepository;
    @Inject
    private ExperimentStepRepository experimentStepRepository;
    @Inject
    private ExperimentProgressRepository experimentProgressRepository;
    @MockBean
    private ObjectStorageService objectStorageService;
    @MockBean
    private FilterService filterService;
    @MockBean
    private ExperimentStepProcessor experimentStepProcessor;

    @Inject
    private ExperimentDataService experimentDataService;

    private InstancesInfo instancesInfo;

    @Override
    public void init() {
        instancesInfo = TestHelperUtils.createInstancesInfo();
        instancesInfoRepository.save(instancesInfo);
    }

    @Override
    public void deleteAll() {
        experimentProgressRepository.deleteAll();
        experimentStepRepository.deleteAll();
        experimentRepository.deleteAll();
        instancesInfoRepository.deleteAll();
    }

    @Test
    void testSuccessRemoveExperimentModel() {
        Experiment experiment =
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW, instancesInfo);
        experiment.setExperimentDownloadUrl(EXPERIMENT_DOWNLOAD_URL);
        experimentRepository.save(experiment);
        experimentDataService.removeExperimentModel(experiment);
        experiment = experimentRepository.findById(experiment.getId()).orElse(null);
        assertThat(experiment).isNotNull();
        assertThat(experiment.getModelPath()).isNull();
        assertThat(experiment.getExperimentDownloadUrl()).isNull();
        assertThat(experiment.getDeletedDate()).isNotNull();
    }

    @Test
    void testSuccessRemoveExperimentTrainingData() {
        Experiment experiment =
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW, instancesInfo);
        experimentRepository.save(experiment);
        experimentDataService.removeExperimentTrainingData(experiment);
        experiment = experimentRepository.findById(experiment.getId()).orElse(null);
        assertThat(experiment).isNotNull();
        assertThat(experiment.getTrainingDataPath()).isNull();
        assertThat(experiment.getDeletedDate()).isNull();
    }

    @Test
    void testRequestsStatusesStatisticsCalculation() {
        experimentRepository.save(
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW, instancesInfo));
        experimentRepository.save(
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW, instancesInfo));
        experimentRepository.save(
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED, instancesInfo));
        experimentRepository.save(
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED, instancesInfo));
        experimentRepository.save(
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED, instancesInfo));
        experimentRepository.save(
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.ERROR, instancesInfo));
        experimentRepository.save(
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.ERROR, instancesInfo));
        experimentRepository.save(
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.ERROR, instancesInfo));
        experimentRepository.save(
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.ERROR, instancesInfo));
        var requestStatusStatisticsDto = experimentDataService.getRequestStatusesStatistics();
        assertThat(requestStatusStatisticsDto).isNotNull();
        assertThat(requestStatusStatisticsDto.getNewRequestsCount()).isEqualTo(2L);
        assertThat(requestStatusStatisticsDto.getInProgressRequestsCount()).isZero();
        assertThat(requestStatusStatisticsDto.getFinishedRequestsCount()).isEqualTo(3L);
        assertThat(requestStatusStatisticsDto.getErrorRequestsCount()).isEqualTo(4L);
        assertThat(requestStatusStatisticsDto.getTimeoutRequestsCount()).isZero();
        assertThat(requestStatusStatisticsDto.getTotalCount()).isEqualTo(experimentRepository.count());
    }

    /**
     * Tests global filtering by search query and experiment status equals to FINISHED.
     */
    @Test
    void testGlobalFilter() {
        Experiment experiment =
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW, instancesInfo);
        Experiment experiment1 =
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED, instancesInfo);
        Experiment experiment2 =
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED, instancesInfo);
        Experiment experiment3 =
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.ERROR, instancesInfo);
        experimentRepository.saveAll(Arrays.asList(experiment, experiment1, experiment2, experiment3));
        PageRequestDto pageRequestDto =
                new PageRequestDto(PAGE_NUMBER, PAGE_SIZE, Experiment_.CREATION_DATE, false,
                        experiment1.getRequestId().substring(4, 10),
                        newArrayList());
        pageRequestDto.getFilters().add(new FilterRequestDto(Experiment_.REQUEST_STATUS,
                Collections.singletonList(RequestStatus.FINISHED.name()), MatchMode.EQUALS));
        when(filterService.getGlobalFilterFields(FilterTemplateType.EXPERIMENT.name())).thenReturn(
                Arrays.asList(Experiment_.EMAIL, Experiment_.REQUEST_ID));
        Page<Experiment> evaluationLogPage = experimentDataService.getNextPage(pageRequestDto);
        assertThat(evaluationLogPage).isNotNull();
        assertThat(evaluationLogPage.getTotalElements()).isOne();
    }

    /**
     * Tests global filtering by requests status field
     */
    @Test
    void testGlobalFilterByRequestStatus() {
        Experiment experiment =
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW, instancesInfo);
        Experiment experiment1 =
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED, instancesInfo);
        Experiment experiment2 =
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.TIMEOUT, instancesInfo);
        Experiment experiment3 =
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.ERROR, instancesInfo);
        experimentRepository.saveAll(Arrays.asList(experiment, experiment1, experiment2, experiment3));
        PageRequestDto pageRequestDto = new PageRequestDto(PAGE_NUMBER, PAGE_SIZE, Experiment_.CREATION_DATE, false,
                RequestStatus.FINISHED.getDescription().substring(0, 2), newArrayList());
        when(filterService.getGlobalFilterFields(FilterTemplateType.EXPERIMENT.name())).thenReturn(
                Collections.singletonList(Experiment_.REQUEST_STATUS));
        Page<Experiment> evaluationLogPage = experimentDataService.getNextPage(pageRequestDto);
        assertThat(evaluationLogPage).isNotNull();
        assertThat(evaluationLogPage.getTotalElements()).isOne();
    }

    /**
     * Tests global filtering by requests status field with empty data result.
     */
    @Test
    void testGlobalFilterByRequestStatusWithEmptyData() {
        Experiment experiment =
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW, instancesInfo);
        Experiment experiment1 =
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED, instancesInfo);
        Experiment experiment2 =
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED, instancesInfo);
        experimentRepository.saveAll(Arrays.asList(experiment, experiment1, experiment2));
        PageRequestDto pageRequestDto = new PageRequestDto(PAGE_NUMBER, PAGE_SIZE, Experiment_.CREATION_DATE, false,
                "query", newArrayList());
        when(filterService.getGlobalFilterFields(FilterTemplateType.EXPERIMENT.name())).thenReturn(
                Collections.singletonList(Experiment_.REQUEST_STATUS));
        Page<Experiment> evaluationLogPage = experimentDataService.getNextPage(pageRequestDto);
        assertThat(evaluationLogPage).isEmpty();
    }

    /**
     * Test filter by experiment type and experiment status order by creation date.
     */
    @Test
    void testExperimentsFilterByTypeAndStatusOrderByCreationDate() {
        Experiment experiment =
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW, instancesInfo);
        experiment.setExperimentType(ExperimentType.ADA_BOOST);
        experiment.setRequestStatus(RequestStatus.NEW);
        experiment.setCreationDate(LocalDateTime.of(2018, 2, 1, 0, 0, 0));
        experimentRepository.save(experiment);
        Experiment experiment1 =
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW, instancesInfo);
        experiment1.setExperimentType(ExperimentType.ADA_BOOST);
        experiment1.setRequestStatus(RequestStatus.NEW);
        experiment1.setCreationDate(LocalDateTime.of(2018, 3, 1, 0, 0, 0));
        experimentRepository.save(experiment1);
        Experiment experiment2 =
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW, instancesInfo);
        experiment2.setExperimentType(ExperimentType.ADA_BOOST);
        experiment2.setRequestStatus(RequestStatus.FINISHED);
        experiment2.setCreationDate(LocalDateTime.of(2018, 1, 1, 12, 0, 0));
        experimentRepository.save(experiment2);
        Experiment experiment3 =
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW, instancesInfo);
        experiment3.setExperimentType(ExperimentType.KNN);
        experiment3.setRequestStatus(RequestStatus.NEW);
        experiment3.setCreationDate(LocalDateTime.of(2018, 1, 1, 9, 0, 0));
        experimentRepository.save(experiment3);
        PageRequestDto pageRequestDto =
                new PageRequestDto(PAGE_NUMBER, PAGE_SIZE, Experiment_.CREATION_DATE, false, null, newArrayList());
        pageRequestDto.getFilters().add(
                new FilterRequestDto(Experiment_.REQUEST_STATUS, Collections.singletonList(RequestStatus.NEW.name()),
                        MatchMode.EQUALS));
        pageRequestDto.getFilters().add(new FilterRequestDto(Experiment_.EXPERIMENT_TYPE,
                Collections.singletonList(ExperimentType.ADA_BOOST.name()), MatchMode.EQUALS));
        Page<Experiment> experiments = experimentDataService.getNextPage(pageRequestDto);
        List<Experiment> experimentList = experiments.getContent();
        assertThat(experiments).isNotNull();
        assertThat(experiments.getTotalElements()).isEqualTo(2);
        assertThat(experimentList).hasSize(2);
        assertThat(experimentList.get(0).getRequestId()).isEqualTo(experiment1.getRequestId());
        assertThat(experimentList.get(1).getRequestId()).isEqualTo(experiment.getRequestId());
    }

    /**
     * Test filter by creation date between order by creation date.
     */
    @Test
    void testExperimentsFilterByCreationDateBetween() {
        Experiment experiment =
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW, instancesInfo);
        experiment.setCreationDate(LocalDateTime.of(2018, 2, 1, 0, 0, 0));
        experimentRepository.save(experiment);
        Experiment experiment1 =
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW, instancesInfo);
        experiment1.setCreationDate(LocalDateTime.of(2018, 3, 1, 0, 0, 0));
        experimentRepository.save(experiment1);
        Experiment experiment2 =
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW, instancesInfo);
        experiment2.setCreationDate(LocalDateTime.of(2018, 6, 1, 12, 0, 0));
        experimentRepository.save(experiment2);
        Experiment experiment3 =
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW, instancesInfo);
        experiment3.setCreationDate(LocalDateTime.of(2018, 7, 1, 9, 0, 0));
        experimentRepository.save(experiment3);
        PageRequestDto pageRequestDto =
                new PageRequestDto(PAGE_NUMBER, PAGE_SIZE, Experiment_.CREATION_DATE, false, null, newArrayList());
        pageRequestDto.getFilters().add(
                new FilterRequestDto(Experiment_.CREATION_DATE, Arrays.asList("2018-01-01", "2018-05-01"),
                        MatchMode.RANGE));
        Page<Experiment> experiments = experimentDataService.getNextPage(pageRequestDto);
        List<Experiment> experimentList = experiments.getContent();
        assertThat(experiments).isNotNull();
        assertThat(experiments.getTotalElements()).isEqualTo(2);
        assertThat(experimentList).hasSize(2);
        assertThat(experimentList.get(0).getRequestId()).isEqualTo(experiment1.getRequestId());
        assertThat(experimentList.get(1).getRequestId()).isEqualTo(experiment.getRequestId());
    }

    /**
     * Test filter by creation date between order by creation date.
     * Case 1: Two days interval
     * Case 2: One day interval
     */
    @Test
    void testExperimentFilterByCreationDateSmallInterval() {
        //Case 1
        Experiment experiment =
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW, instancesInfo);
        experiment.setCreationDate(LocalDateTime.of(2018, 1, 1, 0, 0, 0));
        experimentRepository.save(experiment);
        Experiment experiment1 =
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW, instancesInfo);
        experiment1.setCreationDate(LocalDateTime.of(2018, 1, 1, 12, 0, 0));
        experimentRepository.save(experiment1);
        Experiment experiment2 =
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW, instancesInfo);
        experiment2.setCreationDate(LocalDateTime.of(2018, 1, 2, 23, 59, 59));
        experimentRepository.save(experiment2);
        PageRequestDto pageRequestDto =
                new PageRequestDto(PAGE_NUMBER, PAGE_SIZE, Experiment_.CREATION_DATE, false, null, newArrayList());
        pageRequestDto.getFilters().add(
                new FilterRequestDto(Experiment_.CREATION_DATE, Arrays.asList("2018-01-01", "2018-01-02"),
                        MatchMode.RANGE));
        Page<Experiment> experiments = experimentDataService.getNextPage(pageRequestDto);
        List<Experiment> experimentList = experiments.getContent();
        assertThat(experiments).isNotNull();
        assertThat(experiments.getTotalElements()).isEqualTo(3);
        assertThat(experimentList).hasSize(3);
        experimentRepository.deleteAll();
        //Case 2
        experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW, instancesInfo);
        experiment.setCreationDate(LocalDateTime.of(2018, 1, 1, 0, 0, 0));
        experimentRepository.save(experiment);
        experiment1 = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW, instancesInfo);
        experiment1.setCreationDate(LocalDateTime.of(2018, 1, 1, 23, 59, 59));
        experimentRepository.save(experiment1);
        pageRequestDto =
                new PageRequestDto(PAGE_NUMBER, PAGE_SIZE, Experiment_.CREATION_DATE, false, null, newArrayList());
        pageRequestDto.getFilters().add(
                new FilterRequestDto(Experiment_.CREATION_DATE, Arrays.asList("2018-01-01", "2018-01-01"),
                        MatchMode.RANGE));
        experiments = experimentDataService.getNextPage(pageRequestDto);
        experimentList = experiments.getContent();
        assertThat(experiments).isNotNull();
        assertThat(experiments.getTotalElements()).isEqualTo(2);
        assertThat(experimentList).hasSize(2);
    }

    @Test
    void testGetExperimentTypesStatistics() {
        createAndSaveDataForExperimentsStatistics();
        mockExperimentTypesDictionary();
        var experimentsStatistics =
                experimentDataService.getExperimentsStatistics(LocalDate.of(2018, 1, 1), LocalDate.of(2018, 1, 3));
        Assertions.assertThat(experimentsStatistics).isNotNull();
        assertThat(experimentsStatistics.getTotal()).isEqualTo(3L);
        verifyChartItem(experimentsStatistics.getDataItems(), ExperimentType.ADA_BOOST.name(), 2L);
        verifyChartItem(experimentsStatistics.getDataItems(), ExperimentType.KNN.name(), 1L);
        verifyChartItem(experimentsStatistics.getDataItems(), ExperimentType.DECISION_TREE.name(), 0L);
    }

    @Test
    void testGetExperiment() {
        Experiment experiment =
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW, instancesInfo);
        experimentRepository.save(experiment);
        Experiment actual = experimentDataService.getById(experiment.getId());
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isEqualTo(experiment.getId());
    }

    @Test
    void testGetExperimentShouldThrowEntityNotFoundException() {
        assertThrows(EntityNotFoundException.class, () -> experimentDataService.getById(INVALID_ID));
    }

    @Test
    void testGetExperimentContentUrl() {
        Experiment experiment =
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW, instancesInfo);
        experimentRepository.save(experiment);
        when(objectStorageService.getObjectPresignedProxyUrl(any(GetPresignedUrlObject.class)))
                .thenReturn(EXPERIMENT_DOWNLOAD_URL);
        var s3ContentResponseDto = experimentDataService.getExperimentResultsContentUrl(experiment.getId());
        assertThat(s3ContentResponseDto).isNotNull();
        assertThat(s3ContentResponseDto.getContentUrl()).isEqualTo(EXPERIMENT_DOWNLOAD_URL);
    }

    private void createAndSaveDataForExperimentsStatistics() {
        Experiment experiment =
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW, instancesInfo);
        experiment.setCreationDate(LocalDateTime.of(2018, 1, 1, 0, 0, 0));
        experiment.setExperimentType(ExperimentType.ADA_BOOST);
        Experiment experiment1 =
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW, instancesInfo);
        experiment1.setCreationDate(LocalDateTime.of(2018, 1, 2, 12, 0, 0));
        experiment1.setExperimentType(ExperimentType.ADA_BOOST);
        Experiment experiment2 =
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW, instancesInfo);
        experiment2.setCreationDate(LocalDateTime.of(2018, 1, 3, 23, 59, 59));
        Experiment experiment3 =
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW, instancesInfo);
        experiment3.setCreationDate(LocalDateTime.of(2018, 1, 4, 0, 0, 0));
        experiment3.setExperimentType(ExperimentType.DECISION_TREE);
        experimentRepository.saveAll(List.of(experiment, experiment1, experiment2, experiment3));
    }

    private void mockExperimentTypesDictionary() {
        var experimentsDictionary = new FilterDictionaryDto();
        experimentsDictionary.setValues(newArrayList());
        Stream.of(ExperimentType.values()).forEach(experimentType ->
                experimentsDictionary.getValues().add(new FilterDictionaryValueDto(experimentType.getDescription(),
                        experimentType.name())));
        when(filterService.getFilterDictionary(EXPERIMENT_TYPE)).thenReturn(experimentsDictionary);
    }

    private void verifyChartItem(List<ChartDataDto> items, String classifierName, long expectedCount) {
        var chartItem = items.stream()
                .filter(chartDataDto -> chartDataDto.getName().equals(classifierName))
                .findFirst()
                .orElse(null);
        assertThat(chartItem).isNotNull();
        assertThat(chartItem.getCount()).isEqualTo(expectedCount);
    }
}
