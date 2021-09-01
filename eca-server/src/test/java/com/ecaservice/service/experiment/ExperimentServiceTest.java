package com.ecaservice.service.experiment;

import com.ecaservice.AssertionUtils;
import com.ecaservice.TestHelperUtils;
import com.ecaservice.base.model.ExperimentRequest;
import com.ecaservice.base.model.ExperimentType;
import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.config.AppProperties;
import com.ecaservice.config.CrossValidationConfig;
import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.core.filter.service.FilterService;
import com.ecaservice.exception.experiment.ExperimentException;
import com.ecaservice.mapping.DateTimeConverter;
import com.ecaservice.mapping.ExperimentMapper;
import com.ecaservice.mapping.ExperimentMapperImpl;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.Experiment_;
import com.ecaservice.model.entity.FilterTemplateType;
import com.ecaservice.model.entity.RequestStatus;
import com.ecaservice.model.experiment.InitializationParams;
import com.ecaservice.repository.ExperimentRepository;
import com.ecaservice.service.AbstractJpaTest;
import com.ecaservice.service.evaluation.CalculationExecutorService;
import com.ecaservice.service.evaluation.CalculationExecutorServiceImpl;
import com.ecaservice.web.dto.model.FilterRequestDto;
import com.ecaservice.web.dto.model.MatchMode;
import com.ecaservice.web.dto.model.PageRequestDto;
import eca.converters.model.ExperimentHistory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import weka.core.Instances;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;


/**
 * Unit tests that checks ExperimentService functionality {@see ExperimentService}.
 *
 * @author Roman Batygin
 */
@Import({ExperimentMapperImpl.class, ExperimentConfig.class, AppProperties.class, CrossValidationConfig.class,
        DateTimeConverter.class})
class ExperimentServiceTest extends AbstractJpaTest {

    private static final int PAGE_NUMBER = 0;
    private static final int PAGE_SIZE = 10;
    private static final String INVALID_UUID = "InvalidUuid";

    @Inject
    private ExperimentRepository experimentRepository;
    @Inject
    private ExperimentMapper experimentMapper;
    @Mock
    private DataService dataService;
    @Inject
    private CrossValidationConfig crossValidationConfig;
    @Inject
    private ExperimentConfig experimentConfig;
    @Inject
    private EntityManager entityManager;
    @Inject
    private AppProperties appProperties;
    @Mock
    private FilterService filterService;
    @Mock
    private ExperimentProcessorService experimentProcessorService;

    private ExperimentService experimentService;

    private Instances data;

    @Override
    public void init() {
        data = TestHelperUtils.loadInstances();
        CalculationExecutorService executorService =
                new CalculationExecutorServiceImpl(Executors.newCachedThreadPool());
        experimentService = new ExperimentService(experimentRepository, executorService, experimentMapper, dataService,
                crossValidationConfig, experimentConfig, experimentProcessorService, entityManager, appProperties,
                filterService);
    }

    @Override
    public void deleteAll() {
        experimentRepository.deleteAll();
    }

    @Test
    void testNullTrainingDataPath() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        experiment.setTrainingDataAbsolutePath(null);
        experimentService.processExperiment(experiment);
        assertThat(experiment.getRequestStatus()).isEqualTo(RequestStatus.ERROR);
    }

    @Test
    void testSuccessExperimentRequestCreation() throws Exception {
        ExperimentRequest experimentRequest = TestHelperUtils.createExperimentRequest();
        doNothing().when(dataService).save(any(File.class), any(Instances.class));
        experimentService.createExperiment(experimentRequest);
        List<Experiment> experiments = experimentRepository.findAll();
        AssertionUtils.hasOneElement(experiments);
        Experiment experiment = experiments.get(0);
        assertThat(experiment.getRequestStatus()).isEqualTo(RequestStatus.NEW);
        assertThat(experiment.getRequestId()).isNotNull();
        assertThat(experiment.getCreationDate()).isNotNull();
        assertThat(experiment.getTrainingDataAbsolutePath()).isNotNull();
    }

    @Test
    void testExperimentRequestCreationWithError() throws Exception {
        ExperimentRequest experimentRequest = TestHelperUtils.createExperimentRequest();
        doThrow(Exception.class).when(dataService).save(any(File.class), any(Instances.class));
        assertThrows(ExperimentException.class, () -> experimentService.createExperiment(experimentRequest));
    }

    @Test
    void testProcessExperimentWithSuccessStatus() throws Exception {
        when(dataService.load(any(File.class))).thenReturn(data);
        when(experimentProcessorService.processExperimentHistory(any(Experiment.class),
                any(InitializationParams.class))).thenReturn(new ExperimentHistory());
        doNothing().when(dataService).saveExperimentHistory(any(File.class), any(ExperimentHistory.class));
        experimentService.processExperiment(TestHelperUtils.createExperiment(UUID.randomUUID().toString()));
        List<Experiment> experiments = experimentRepository.findAll();
        AssertionUtils.hasOneElement(experiments);
        Experiment experiment = experiments.iterator().next();
        assertThat(experiment.getEndDate()).isNotNull();
        assertThat(experiment.getExperimentAbsolutePath()).isNotNull();
        assertThat(experiment.getToken()).isNotNull();
        assertThat(experiment.getRequestStatus()).isEqualTo(RequestStatus.FINISHED);
    }

    @Test
    void testProcessExperimentWithErrorStatus() throws Exception {
        when(dataService.load(any(File.class))).thenThrow(new Exception());
        experimentService.processExperiment(TestHelperUtils.createExperiment(UUID.randomUUID().toString()));
        List<Experiment> experiments = experimentRepository.findAll();
        AssertionUtils.hasOneElement(experiments);
        Experiment experiment = experiments.iterator().next();
        assertThat(experiment.getEndDate()).isNotNull();
        assertThat(experiment.getRequestStatus()).isEqualTo(RequestStatus.ERROR);
    }

    @Test
    void testProcessExperimentWithTimeoutStatus() throws Exception {
        when(dataService.load(any(File.class))).thenReturn(data);
        when(experimentProcessorService.processExperimentHistory(any(Experiment.class),
                any(InitializationParams.class))).thenReturn(new ExperimentHistory());
        doThrow(TimeoutException.class).when(dataService).saveExperimentHistory(any(File.class),
                any(ExperimentHistory.class));
        experimentService.processExperiment(TestHelperUtils.createExperiment(UUID.randomUUID().toString()));
        List<Experiment> experiments = experimentRepository.findAll();
        AssertionUtils.hasOneElement(experiments);
        Experiment experiment = experiments.iterator().next();
        assertThat(experiment.getEndDate()).isNotNull();
        assertThat(experiment.getRequestStatus()).isEqualTo(RequestStatus.TIMEOUT);
    }

    @Test
    void testSuccessRemoveExperimentModel() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        experimentRepository.save(experiment);
        experimentService.removeExperimentModel(experiment);
        experiment = experimentRepository.findById(experiment.getId()).orElse(null);
        assertThat(experiment).isNotNull();
        assertThat(experiment.getExperimentAbsolutePath()).isNull();
        assertThat(experiment.getDeletedDate()).isNotNull();
    }

    @Test
    void testSuccessRemoveExperimentTrainingData() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        experimentRepository.save(experiment);
        experimentService.removeExperimentTrainingData(experiment);
        experiment = experimentRepository.findById(experiment.getId()).orElse(null);
        assertThat(experiment).isNotNull();
        assertThat(experiment.getTrainingDataAbsolutePath()).isNull();
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
        Map<RequestStatus, Long> requestStatusesMap = experimentService.getRequestStatusesStatistics();
        assertThat(requestStatusesMap)
                .isNotNull()
                .hasSameSizeAs(RequestStatus.values())
                .containsEntry(RequestStatus.NEW, 2L)
                .containsEntry(RequestStatus.FINISHED, 3L)
                .containsEntry(RequestStatus.ERROR, 4L)
                .containsEntry(RequestStatus.TIMEOUT, 0L);
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
                Arrays.asList(Experiment_.EMAIL, Experiment_.FIRST_NAME, Experiment_.REQUEST_ID));
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
        assertThat(experimentList.size()).isEqualTo(2);
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
        assertThat(experimentList.size()).isEqualTo(2);
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
        assertThat(experimentList.size()).isEqualTo(2);
    }

    @Test
    void testGetExperimentTypesStatistics() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW);
        experiment.setCreationDate(LocalDateTime.of(2018, 1, 1, 0, 0, 0));
        experiment.setExperimentType(ExperimentType.ADA_BOOST);
        experimentRepository.save(experiment);
        Experiment experiment1 = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW);
        experiment1.setCreationDate(LocalDateTime.of(2018, 1, 2, 12, 0, 0));
        experiment1.setExperimentType(ExperimentType.ADA_BOOST);
        experimentRepository.save(experiment1);
        Experiment experiment2 = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW);
        experiment2.setCreationDate(LocalDateTime.of(2018, 1, 3, 23, 59, 59));
        experimentRepository.save(experiment2);
        Experiment experiment3 = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW);
        experiment3.setCreationDate(LocalDateTime.of(2018, 1, 4, 0, 0, 0));
        experiment3.setExperimentType(ExperimentType.DECISION_TREE);
        experimentRepository.save(experiment3);
        Map<ExperimentType, Long> experimentTypesMap =
                experimentService.getExperimentTypesStatistics(LocalDate.of(2018, 1, 1), LocalDate.of(2018, 1, 3));
        Assertions.assertThat(experimentTypesMap).isNotNull();
        Assertions.assertThat(experimentTypesMap)
                .hasSameSizeAs(ExperimentType.values())
                .containsEntry(ExperimentType.ADA_BOOST, 2L);
        Assertions.assertThat(experimentTypesMap.get(ExperimentType.KNN)).isOne();
        Assertions.assertThat(experimentTypesMap.get(ExperimentType.DECISION_TREE)).isZero();
    }

    @Test
    void testGetExperiment() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        experimentRepository.save(experiment);
        Experiment actual = experimentService.getByRequestId(experiment.getRequestId());
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isEqualTo(experiment.getId());
    }

    @Test
    void testGetExperimentShouldThrowEntityNotFoundException() {
        assertThrows(EntityNotFoundException.class, () -> experimentService.getByRequestId(INVALID_UUID));
    }
}
