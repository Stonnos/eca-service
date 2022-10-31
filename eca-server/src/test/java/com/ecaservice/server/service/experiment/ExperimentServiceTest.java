package com.ecaservice.server.service.experiment;

import com.ecaservice.base.model.ExperimentRequest;
import com.ecaservice.base.model.ExperimentType;
import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.core.filter.service.FilterService;
import com.ecaservice.s3.client.minio.exception.ObjectStorageException;
import com.ecaservice.s3.client.minio.model.GetPresignedUrlObject;
import com.ecaservice.s3.client.minio.service.ObjectStorageService;
import com.ecaservice.server.AssertionUtils;
import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.config.AppProperties;
import com.ecaservice.server.config.CrossValidationConfig;
import com.ecaservice.server.config.ExperimentConfig;
import com.ecaservice.server.exception.experiment.ExperimentException;
import com.ecaservice.server.mapping.DateTimeConverter;
import com.ecaservice.server.mapping.ExperimentMapperImpl;
import com.ecaservice.server.mapping.InstancesInfoMapperImpl;
import com.ecaservice.server.model.MsgProperties;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.ExperimentStep;
import com.ecaservice.server.model.entity.ExperimentStepEntity;
import com.ecaservice.server.model.entity.ExperimentStepStatus;
import com.ecaservice.server.model.entity.Experiment_;
import com.ecaservice.server.model.entity.FilterTemplateType;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.repository.ExperimentRepository;
import com.ecaservice.server.repository.ExperimentStepRepository;
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
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ecaservice.server.TestHelperUtils.createMessageProperties;
import static com.ecaservice.server.service.filter.dictionary.FilterDictionaries.EXPERIMENT_TYPE;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;


/**
 * Unit tests that checks ExperimentService functionality {@see ExperimentService}.
 *
 * @author Roman Batygin
 */
@Import({ExperimentMapperImpl.class, ExperimentConfig.class, AppProperties.class, CrossValidationConfig.class,
        DateTimeConverter.class, InstancesInfoMapperImpl.class, ExperimentService.class})
class ExperimentServiceTest extends AbstractJpaTest {

    private static final int PAGE_NUMBER = 0;
    private static final int PAGE_SIZE = 10;
    private static final long INVALID_ID = 1000L;
    private static final String EXPERIMENT_DOWNLOAD_URL = "http://localhost:9000/experiment";

    @Inject
    private ExperimentRepository experimentRepository;
    @Inject
    private ExperimentStepRepository experimentStepRepository;
    @MockBean
    private ObjectStorageService objectStorageService;
    @MockBean
    private FilterService filterService;
    @MockBean
    private ExperimentStepProcessor experimentStepProcessor;

    @Inject
    private ExperimentService experimentService;

    @Override
    public void deleteAll() {
        experimentStepRepository.deleteAll();
        experimentRepository.deleteAll();
    }

    @Test
    void testSuccessExperimentRequestCreation() {
        ExperimentRequest experimentRequest = TestHelperUtils.createExperimentRequest();
        MsgProperties msgProperties = createMessageProperties();
        experimentService.createExperiment(experimentRequest, msgProperties);
        List<Experiment> experiments = experimentRepository.findAll();
        AssertionUtils.hasOneElement(experiments);
        Experiment experiment = experiments.iterator().next();
        assertThat(experiment.getRequestStatus()).isEqualTo(RequestStatus.NEW);
        assertThat(experiment.getRequestId()).isNotNull();
        assertThat(experiment.getCreationDate()).isNotNull();
        assertThat(experiment.getChannel()).isEqualTo(msgProperties.getChannel());
        assertThat(experiment.getReplyTo()).isEqualTo(msgProperties.getReplyTo());
        assertThat(experiment.getCorrelationId()).isEqualTo(msgProperties.getCorrelationId());
        assertThat(experiment.getTrainingDataPath()).isNotNull();
    }

    @Test
    void testExperimentRequestCreationWithError() throws IOException {
        ExperimentRequest experimentRequest = TestHelperUtils.createExperimentRequest();
        doThrow(ObjectStorageException.class)
                .when(objectStorageService)
                .uploadObject(any(Serializable.class), anyString());
        MsgProperties msgProperties = createMessageProperties();
        assertThrows(ExperimentException.class,
                () -> experimentService.createExperiment(experimentRequest, msgProperties));
    }

    @Test
    void testStartExperiment() {
        var experiment = createAndSaveExperiment(RequestStatus.NEW);
        experimentService.startExperiment(experiment);
        var actual = experimentRepository.findById(experiment.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getRequestStatus()).isEqualTo(RequestStatus.IN_PROGRESS);
        assertThat(actual.getStartDate()).isNotNull();
        verifySavedSteps(experiment);
    }

    @Test
    void testFinishExperimentWith() {
        var experiment = createAndSaveExperiment(RequestStatus.IN_PROGRESS);
        createAndSaveExperimentStep(experiment, ExperimentStep.EXPERIMENT_PROCESSING, ExperimentStepStatus.COMPLETED);
        createAndSaveExperimentStep(experiment, ExperimentStep.UPLOAD_EXPERIMENT_MODEL, ExperimentStepStatus.COMPLETED);
        createAndSaveExperimentStep(experiment, ExperimentStep.GET_EXPERIMENT_DOWNLOAD_URL,
                ExperimentStepStatus.COMPLETED);
        internalTestFinishExperiment(experiment, RequestStatus.FINISHED);
    }

    @Test
    void testFinishExperimentWithError() {
        var experiment = createAndSaveExperiment(RequestStatus.IN_PROGRESS);
        createAndSaveExperimentStep(experiment, ExperimentStep.EXPERIMENT_PROCESSING, ExperimentStepStatus.COMPLETED);
        createAndSaveExperimentStep(experiment, ExperimentStep.UPLOAD_EXPERIMENT_MODEL, ExperimentStepStatus.ERROR);
        createAndSaveExperimentStep(experiment, ExperimentStep.GET_EXPERIMENT_DOWNLOAD_URL,
                ExperimentStepStatus.CANCELED);
        internalTestFinishExperiment(experiment, RequestStatus.ERROR);
    }

    @Test
    void testFinishExperimentWithTimeout() {
        var experiment = createAndSaveExperiment(RequestStatus.IN_PROGRESS);
        createAndSaveExperimentStep(experiment, ExperimentStep.EXPERIMENT_PROCESSING, ExperimentStepStatus.COMPLETED);
        createAndSaveExperimentStep(experiment, ExperimentStep.UPLOAD_EXPERIMENT_MODEL, ExperimentStepStatus.TIMEOUT);
        createAndSaveExperimentStep(experiment, ExperimentStep.GET_EXPERIMENT_DOWNLOAD_URL,
                ExperimentStepStatus.CANCELED);
        internalTestFinishExperiment(experiment, RequestStatus.TIMEOUT);
    }

    @Test
    void testFinishExperimentWithInvalidStepStatuses() {
        var experiment = createAndSaveExperiment(RequestStatus.IN_PROGRESS);
        createAndSaveExperimentStep(experiment, ExperimentStep.EXPERIMENT_PROCESSING, ExperimentStepStatus.READY);
        createAndSaveExperimentStep(experiment, ExperimentStep.UPLOAD_EXPERIMENT_MODEL, ExperimentStepStatus.FAILED);
        assertThrows(ExperimentException.class, () -> experimentService.finishExperiment(experiment));
    }

    @Test
    void testSuccessRemoveExperimentModel() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        experiment.setExperimentDownloadUrl(EXPERIMENT_DOWNLOAD_URL);
        experimentRepository.save(experiment);
        experimentService.removeExperimentModel(experiment);
        experiment = experimentRepository.findById(experiment.getId()).orElse(null);
        assertThat(experiment).isNotNull();
        assertThat(experiment.getExperimentPath()).isNull();
        assertThat(experiment.getExperimentDownloadUrl()).isNull();
        assertThat(experiment.getDeletedDate()).isNotNull();
    }

    @Test
    void testSuccessRemoveExperimentTrainingData() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        experimentRepository.save(experiment);
        experimentService.removeExperimentTrainingData(experiment);
        experiment = experimentRepository.findById(experiment.getId()).orElse(null);
        assertThat(experiment).isNotNull();
        assertThat(experiment.getTrainingDataPath()).isNull();
        assertThat(experiment.getDeletedDate()).isNull();
    }

    @Test
    void testRequestsStatusesStatisticsCalculation() {
        experimentRepository.save(TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW));
        experimentRepository.save(TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW));
        experimentRepository.save(
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED));
        experimentRepository.save(
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED));
        experimentRepository.save(
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED));
        experimentRepository.save(TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.ERROR));
        experimentRepository.save(TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.ERROR));
        experimentRepository.save(TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.ERROR));
        experimentRepository.save(TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.ERROR));
        var requestStatusStatisticsDto = experimentService.getRequestStatusesStatistics();
        assertThat(requestStatusStatisticsDto).isNotNull();
        assertThat(requestStatusStatisticsDto.getNewRequestsCount()).isEqualTo(2L);
        assertThat(requestStatusStatisticsDto.getInProgressRequestsCount()).isEqualTo(0L);
        assertThat(requestStatusStatisticsDto.getFinishedRequestsCount()).isEqualTo(3L);
        assertThat(requestStatusStatisticsDto.getErrorRequestsCount()).isEqualTo(4L);
        assertThat(requestStatusStatisticsDto.getTimeoutRequestsCount()).isEqualTo(0L);
        assertThat(requestStatusStatisticsDto.getTotalCount()).isEqualTo(experimentRepository.count());
    }

    /**
     * Tests global filtering by search query and experiment status equals to FINISHED.
     */
    @Test
    void testGlobalFilter() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW);
        Experiment experiment1 = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        Experiment experiment2 = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        Experiment experiment3 = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.ERROR);
        experimentRepository.saveAll(Arrays.asList(experiment, experiment1, experiment2, experiment3));
        PageRequestDto pageRequestDto =
                new PageRequestDto(PAGE_NUMBER, PAGE_SIZE, Experiment_.CREATION_DATE, false,
                        experiment1.getRequestId().substring(4, 10),
                        newArrayList());
        pageRequestDto.getFilters().add(new FilterRequestDto(Experiment_.REQUEST_STATUS,
                Collections.singletonList(RequestStatus.FINISHED.name()), MatchMode.EQUALS));
        when(filterService.getGlobalFilterFields(FilterTemplateType.EXPERIMENT.name())).thenReturn(
                Arrays.asList(Experiment_.EMAIL, Experiment_.REQUEST_ID));
        Page<Experiment> evaluationLogPage = experimentService.getNextPage(pageRequestDto);
        assertThat(evaluationLogPage).isNotNull();
        assertThat(evaluationLogPage.getTotalElements()).isOne();
    }

    /**
     * Tests global filtering by requests status field
     */
    @Test
    void testGlobalFilterByRequestStatus() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW);
        Experiment experiment1 = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        Experiment experiment2 = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.TIMEOUT);
        Experiment experiment3 = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.ERROR);
        experimentRepository.saveAll(Arrays.asList(experiment, experiment1, experiment2, experiment3));
        PageRequestDto pageRequestDto = new PageRequestDto(PAGE_NUMBER, PAGE_SIZE, Experiment_.CREATION_DATE, false,
                RequestStatus.FINISHED.getDescription().substring(0, 2), newArrayList());
        when(filterService.getGlobalFilterFields(FilterTemplateType.EXPERIMENT.name())).thenReturn(
                Collections.singletonList(Experiment_.REQUEST_STATUS));
        Page<Experiment> evaluationLogPage = experimentService.getNextPage(pageRequestDto);
        assertThat(evaluationLogPage).isNotNull();
        assertThat(evaluationLogPage.getTotalElements()).isOne();
    }

    /**
     * Tests global filtering by requests status field with empty data result.
     */
    @Test
    void testGlobalFilterByRequestStatusWithEmptyData() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW);
        Experiment experiment1 = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        Experiment experiment2 = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        experimentRepository.saveAll(Arrays.asList(experiment, experiment1, experiment2));
        PageRequestDto pageRequestDto = new PageRequestDto(PAGE_NUMBER, PAGE_SIZE, Experiment_.CREATION_DATE, false,
                "query", newArrayList());
        when(filterService.getGlobalFilterFields(FilterTemplateType.EXPERIMENT.name())).thenReturn(
                Collections.singletonList(Experiment_.REQUEST_STATUS));
        Page<Experiment> evaluationLogPage = experimentService.getNextPage(pageRequestDto);
        assertThat(evaluationLogPage).isEmpty();
    }

    /**
     * Test filter by experiment type and experiment status order by creation date.
     */
    @Test
    void testExperimentsFilterByTypeAndStatusOrderByCreationDate() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW);
        experiment.setExperimentType(ExperimentType.ADA_BOOST);
        experiment.setRequestStatus(RequestStatus.NEW);
        experiment.setCreationDate(LocalDateTime.of(2018, 2, 1, 0, 0, 0));
        experimentRepository.save(experiment);
        Experiment experiment1 = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW);
        experiment1.setExperimentType(ExperimentType.ADA_BOOST);
        experiment1.setRequestStatus(RequestStatus.NEW);
        experiment1.setCreationDate(LocalDateTime.of(2018, 3, 1, 0, 0, 0));
        experimentRepository.save(experiment1);
        Experiment experiment2 = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW);
        experiment2.setExperimentType(ExperimentType.ADA_BOOST);
        experiment2.setRequestStatus(RequestStatus.FINISHED);
        experiment2.setCreationDate(LocalDateTime.of(2018, 1, 1, 12, 0, 0));
        experimentRepository.save(experiment2);
        Experiment experiment3 = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW);
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
        Page<Experiment> experiments = experimentService.getNextPage(pageRequestDto);
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
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW);
        experiment.setCreationDate(LocalDateTime.of(2018, 2, 1, 0, 0, 0));
        experimentRepository.save(experiment);
        Experiment experiment1 = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW);
        experiment1.setCreationDate(LocalDateTime.of(2018, 3, 1, 0, 0, 0));
        experimentRepository.save(experiment1);
        Experiment experiment2 = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW);
        experiment2.setCreationDate(LocalDateTime.of(2018, 6, 1, 12, 0, 0));
        experimentRepository.save(experiment2);
        Experiment experiment3 = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW);
        experiment3.setCreationDate(LocalDateTime.of(2018, 7, 1, 9, 0, 0));
        experimentRepository.save(experiment3);
        PageRequestDto pageRequestDto =
                new PageRequestDto(PAGE_NUMBER, PAGE_SIZE, Experiment_.CREATION_DATE, false, null, newArrayList());
        pageRequestDto.getFilters().add(
                new FilterRequestDto(Experiment_.CREATION_DATE, Arrays.asList("2018-01-01", "2018-05-01"),
                        MatchMode.RANGE));
        Page<Experiment> experiments = experimentService.getNextPage(pageRequestDto);
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
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW);
        experiment.setCreationDate(LocalDateTime.of(2018, 1, 1, 0, 0, 0));
        experimentRepository.save(experiment);
        Experiment experiment1 = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW);
        experiment1.setCreationDate(LocalDateTime.of(2018, 1, 1, 12, 0, 0));
        experimentRepository.save(experiment1);
        Experiment experiment2 = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW);
        experiment2.setCreationDate(LocalDateTime.of(2018, 1, 2, 23, 59, 59));
        experimentRepository.save(experiment2);
        PageRequestDto pageRequestDto =
                new PageRequestDto(PAGE_NUMBER, PAGE_SIZE, Experiment_.CREATION_DATE, false, null, newArrayList());
        pageRequestDto.getFilters().add(
                new FilterRequestDto(Experiment_.CREATION_DATE, Arrays.asList("2018-01-01", "2018-01-02"),
                        MatchMode.RANGE));
        Page<Experiment> experiments = experimentService.getNextPage(pageRequestDto);
        List<Experiment> experimentList = experiments.getContent();
        assertThat(experiments).isNotNull();
        assertThat(experiments.getTotalElements()).isEqualTo(3);
        assertThat(experimentList.size()).isEqualTo(3);
        experimentRepository.deleteAll();
        //Case 2
        experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW);
        experiment.setCreationDate(LocalDateTime.of(2018, 1, 1, 0, 0, 0));
        experimentRepository.save(experiment);
        experiment1 = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW);
        experiment1.setCreationDate(LocalDateTime.of(2018, 1, 1, 23, 59, 59));
        experimentRepository.save(experiment1);
        pageRequestDto =
                new PageRequestDto(PAGE_NUMBER, PAGE_SIZE, Experiment_.CREATION_DATE, false, null, newArrayList());
        pageRequestDto.getFilters().add(
                new FilterRequestDto(Experiment_.CREATION_DATE, Arrays.asList("2018-01-01", "2018-01-01"),
                        MatchMode.RANGE));
        experiments = experimentService.getNextPage(pageRequestDto);
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
                experimentService.getExperimentsStatistics(LocalDate.of(2018, 1, 1), LocalDate.of(2018, 1, 3));
        Assertions.assertThat(experimentsStatistics).isNotNull();
        assertThat(experimentsStatistics.getTotal()).isEqualTo(3L);
        verifyChartItem(experimentsStatistics.getDataItems(), ExperimentType.ADA_BOOST.name(), 2L);
        verifyChartItem(experimentsStatistics.getDataItems(), ExperimentType.KNN.name(), 1L);
        verifyChartItem(experimentsStatistics.getDataItems(), ExperimentType.DECISION_TREE.name(), 0L);
    }

    @Test
    void testGetExperiment() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        experimentRepository.save(experiment);
        Experiment actual = experimentService.getById(experiment.getId());
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isEqualTo(experiment.getId());
    }

    @Test
    void testGetExperimentShouldThrowEntityNotFoundException() {
        assertThrows(EntityNotFoundException.class, () -> experimentService.getById(INVALID_ID));
    }

    @Test
    void testGetExperimentContentUrl() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        experimentRepository.save(experiment);
        when(objectStorageService.getObjectPresignedProxyUrl(any(GetPresignedUrlObject.class)))
                .thenReturn(EXPERIMENT_DOWNLOAD_URL);
        var s3ContentResponseDto = experimentService.getExperimentResultsContentUrl(experiment.getId());
        assertThat(s3ContentResponseDto).isNotNull();
        assertThat(s3ContentResponseDto.getContentUrl()).isEqualTo(EXPERIMENT_DOWNLOAD_URL);
    }

    private void createAndSaveDataForExperimentsStatistics() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW);
        experiment.setCreationDate(LocalDateTime.of(2018, 1, 1, 0, 0, 0));
        experiment.setExperimentType(ExperimentType.ADA_BOOST);
        Experiment experiment1 = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW);
        experiment1.setCreationDate(LocalDateTime.of(2018, 1, 2, 12, 0, 0));
        experiment1.setExperimentType(ExperimentType.ADA_BOOST);
        Experiment experiment2 = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW);
        experiment2.setCreationDate(LocalDateTime.of(2018, 1, 3, 23, 59, 59));
        Experiment experiment3 = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW);
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

    private Experiment createAndSaveExperiment(RequestStatus requestStatus) {
        var experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), requestStatus);
        return experimentRepository.save(experiment);
    }

    private void createAndSaveExperimentStep(Experiment experiment,
                                             ExperimentStep experimentStep,
                                             ExperimentStepStatus stepStatus) {
        var experimentStepEntity = TestHelperUtils.createExperimentStepEntity(experiment, experimentStep, stepStatus);
        experimentStepRepository.save(experimentStepEntity);
    }

    private void verifySavedSteps(Experiment experiment) {
        var steps = experimentStepRepository.findAll()
                .stream()
                .collect(Collectors.toMap(ExperimentStepEntity::getStep, Function.identity()));
        assertThat(steps.size()).isEqualTo(ExperimentStep.values().length);
        Stream.of(ExperimentStep.values()).forEach(experimentStep -> {
            var step = steps.get(experimentStep);
            assertThat(step).isNotNull();
            assertThat(step.getStep()).isEqualTo(experimentStep);
            assertThat(step.getStepOrder()).isEqualTo(experimentStep.ordinal());
            assertThat(step.getStatus()).isEqualTo(ExperimentStepStatus.READY);
            assertThat(step.getExperiment()).isNotNull();
            assertThat(step.getExperiment().getId()).isEqualTo(experiment.getId());
        });
    }

    private void internalTestFinishExperiment(Experiment experiment, RequestStatus expectedStatus) {
        experimentService.finishExperiment(experiment);
        var actual = experimentRepository.findById(experiment.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getRequestStatus()).isEqualTo(expectedStatus);
        assertThat(actual.getEndDate()).isNotNull();
    }
}
