package com.ecaservice.service.experiment;

import com.ecaservice.AssertionUtils;
import com.ecaservice.TestHelperUtils;
import com.ecaservice.config.CommonConfig;
import com.ecaservice.config.CrossValidationConfig;
import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.dto.ExperimentRequest;
import com.ecaservice.exception.experiment.ExperimentException;
import com.ecaservice.mapping.ExperimentMapper;
import com.ecaservice.mapping.ExperimentMapperImpl;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.Experiment_;
import com.ecaservice.model.entity.FilterTemplateType;
import com.ecaservice.model.entity.RequestStatus;
import com.ecaservice.model.experiment.ExperimentType;
import com.ecaservice.model.experiment.InitializationParams;
import com.ecaservice.repository.ExperimentRepository;
import com.ecaservice.service.AbstractJpaTest;
import com.ecaservice.service.evaluation.CalculationExecutorService;
import com.ecaservice.service.evaluation.CalculationExecutorServiceImpl;
import com.ecaservice.service.filter.FilterService;
import com.ecaservice.web.dto.model.FilterRequestDto;
import com.ecaservice.web.dto.model.MatchMode;
import com.ecaservice.web.dto.model.PageRequestDto;
import eca.converters.model.ExperimentHistory;
import org.assertj.core.api.Assertions;
import org.junit.Test;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;


/**
 * Unit tests that checks ExperimentService functionality {@see ExperimentService}.
 *
 * @author Roman Batygin
 */
@Import({ExperimentMapperImpl.class, ExperimentConfig.class, CommonConfig.class, CrossValidationConfig.class})
public class ExperimentServiceTest extends AbstractJpaTest {

    private static final int PAGE_NUMBER = 0;
    private static final int PAGE_SIZE = 10;

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
    private CommonConfig commonConfig;
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
                crossValidationConfig, experimentConfig, experimentProcessorService, entityManager, commonConfig,
                filterService);
    }

    @Override
    public void deleteAll() {
        experimentRepository.deleteAll();
    }

    @Test
    public void testNullTrainingDataPath() {
        Experiment experiment = new Experiment();
        experimentService.processExperiment(experiment);
        assertThat(experiment.getExperimentStatus()).isEqualTo(RequestStatus.ERROR);
    }

    @Test
    public void testSuccessExperimentRequestCreation() throws Exception {
        ExperimentRequest experimentRequest = TestHelperUtils.createExperimentRequest();
        doNothing().when(dataService).save(any(File.class), any(Instances.class));
        experimentService.createExperiment(experimentRequest);
        List<Experiment> experiments = experimentRepository.findAll();
        AssertionUtils.hasOneElement(experiments);
        Experiment experiment = experiments.get(0);
        assertThat(experiment.getExperimentStatus()).isEqualTo(RequestStatus.NEW);
        assertThat(experiment.getUuid()).isNotNull();
        assertThat(experiment.getCreationDate()).isNotNull();
        assertThat(experiment.getTrainingDataAbsolutePath()).isNotNull();
    }

    @Test(expected = ExperimentException.class)
    public void testExperimentRequestCreationWithError() throws Exception {
        ExperimentRequest experimentRequest = TestHelperUtils.createExperimentRequest();
        doThrow(Exception.class).when(dataService).save(any(File.class), any(Instances.class));
        experimentService.createExperiment(experimentRequest);
    }

    @Test
    public void testProcessExperimentWithSuccessStatus() throws Exception {
        when(dataService.load(any(File.class))).thenReturn(data);
        when(experimentProcessorService.processExperimentHistory(any(Experiment.class),
                any(InitializationParams.class))).thenReturn(new ExperimentHistory());
        doNothing().when(dataService).saveExperimentHistory(any(File.class), any(ExperimentHistory.class));
        experimentService.processExperiment(TestHelperUtils.createExperiment(null));
        List<Experiment> experiments = experimentRepository.findAll();
        AssertionUtils.hasOneElement(experiments);
        Experiment experiment = experiments.get(0);
        assertThat(experiment.getStartDate()).isNotNull();
        assertThat(experiment.getEndDate()).isNotNull();
        assertThat(experiment.getExperimentAbsolutePath()).isNotNull();
        assertThat(experiment.getToken()).isNotNull();
        assertThat(experiment.getExperimentStatus()).isEqualTo(RequestStatus.FINISHED);
    }

    @Test
    public void testProcessExperimentWithErrorStatus() throws Exception {
        when(dataService.load(any(File.class))).thenThrow(new Exception());
        experimentService.processExperiment(TestHelperUtils.createExperiment(null));
        List<Experiment> experiments = experimentRepository.findAll();
        AssertionUtils.hasOneElement(experiments);
        Experiment experiment = experiments.get(0);
        assertThat(experiment.getStartDate()).isNotNull();
        assertThat(experiment.getEndDate()).isNotNull();
        assertThat(experiment.getExperimentStatus()).isEqualTo(RequestStatus.ERROR);
    }

    @Test
    public void testProcessExperimentWithTimeoutStatus() throws Exception {
        when(dataService.load(any(File.class))).thenReturn(data);
        when(experimentProcessorService.processExperimentHistory(any(Experiment.class),
                any(InitializationParams.class))).thenReturn(new ExperimentHistory());
        doThrow(TimeoutException.class).when(dataService).saveExperimentHistory(any(File.class),
                any(ExperimentHistory.class));
        experimentService.processExperiment(TestHelperUtils.createExperiment(null));
        List<Experiment> experiments = experimentRepository.findAll();
        AssertionUtils.hasOneElement(experiments);
        Experiment experiment = experiments.get(0);
        assertThat(experiment.getStartDate()).isNotNull();
        assertThat(experiment.getEndDate()).isNotNull();
        assertThat(experiment.getExperimentStatus()).isEqualTo(RequestStatus.TIMEOUT);
    }

    @Test
    public void testSuccessRemoveExperiment() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        experimentRepository.save(experiment);
        when(dataService.delete(any(File.class))).thenReturn(true);
        experimentService.removeExperimentData(experiment);
        experiment = experimentRepository.findById(experiment.getId()).orElse(null);
        assertThat(experiment).isNotNull();
        assertThat(experiment.getExperimentAbsolutePath()).isNull();
        assertThat(experiment.getTrainingDataAbsolutePath()).isNull();
        assertThat(experiment.getDeletedDate()).isNotNull();
    }

    /**
     * Case 1: Experiment training data isn't removed. Expected -> experiment results isn't removed
     * and deleted date is null
     * Case 2: experiment results file isn't removed. Expected -> Deleted date is null
     */
    @Test
    public void testRemoveExperimentFailed() {
        //Case 1
        Experiment createdExperiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        experimentRepository.save(createdExperiment);
        when(dataService.delete(any(File.class))).thenReturn(false).thenReturn(true);
        experimentService.removeExperimentData(createdExperiment);
        Experiment expectedExperiment = experimentRepository.findById(createdExperiment.getId()).orElse(null);
        assertThat(expectedExperiment).isNotNull();
        assertThat(expectedExperiment.getExperimentAbsolutePath()).isNotNull();
        assertThat(expectedExperiment.getTrainingDataAbsolutePath()).isNotNull();
        assertThat(expectedExperiment.getDeletedDate()).isNull();
        //Case 2
        createdExperiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        when(dataService.delete(any(File.class))).thenReturn(true).thenReturn(false);
        experimentService.removeExperimentData(createdExperiment);
        expectedExperiment = experimentRepository.findById(createdExperiment.getId()).orElse(null);
        assertThat(expectedExperiment).isNotNull();
        assertThat(expectedExperiment.getExperimentAbsolutePath()).isNotNull();
        assertThat(expectedExperiment.getTrainingDataAbsolutePath()).isNull();
        assertThat(expectedExperiment.getDeletedDate()).isNull();
    }

    @Test
    public void testRequestsStatusesStatisticsCalculation() {
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
        assertThat(requestStatusesMap).isNotNull();
        assertThat(requestStatusesMap.size()).isEqualTo(RequestStatus.values().length);
        assertThat(requestStatusesMap.get(RequestStatus.NEW)).isEqualTo(2L);
        assertThat(requestStatusesMap.get(RequestStatus.FINISHED)).isEqualTo(3L);
        assertThat(requestStatusesMap.get(RequestStatus.ERROR)).isEqualTo(4L);
        assertThat(requestStatusesMap.get(RequestStatus.TIMEOUT)).isZero();
    }

    /**
     * Tests global filtering by search query and experiment status equals to FINISHED.
     */
    @Test
    public void testGlobalFilter() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW);
        Experiment experiment1 = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        Experiment experiment2 = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        Experiment experiment3 = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.ERROR);
        experimentRepository.saveAll(Arrays.asList(experiment, experiment1, experiment2, experiment3));
        PageRequestDto pageRequestDto =
                new PageRequestDto(PAGE_NUMBER, PAGE_SIZE, Experiment_.CREATION_DATE, false,
                        experiment1.getUuid().substring(4, 10),
                        newArrayList());
        pageRequestDto.getFilters().add(new FilterRequestDto(Experiment_.EXPERIMENT_STATUS,
                Collections.singletonList(RequestStatus.FINISHED.name()), MatchMode.EQUALS));
        when(filterService.getGlobalFilterFields(FilterTemplateType.EXPERIMENT)).thenReturn(
                Arrays.asList(Experiment_.EMAIL, Experiment_.FIRST_NAME, Experiment_.UUID));
        Page<Experiment> evaluationLogPage = experimentService.getNextPage(pageRequestDto);
        assertThat(evaluationLogPage).isNotNull();
        assertThat(evaluationLogPage.getTotalElements()).isOne();
    }

    /**
     * Tests global filtering by requests status field
     */
    @Test
    public void testGlobalFilterByRequestStatus() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW);
        Experiment experiment1 = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        Experiment experiment2 = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.TIMEOUT);
        Experiment experiment3 = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.ERROR);
        experimentRepository.saveAll(Arrays.asList(experiment, experiment1, experiment2, experiment3));
        PageRequestDto pageRequestDto = new PageRequestDto(PAGE_NUMBER, PAGE_SIZE, Experiment_.CREATION_DATE, false,
                RequestStatus.FINISHED.getDescription().substring(0, 2), newArrayList());
        when(filterService.getGlobalFilterFields(FilterTemplateType.EXPERIMENT)).thenReturn(
                Collections.singletonList(Experiment_.EXPERIMENT_STATUS));
        Page<Experiment> evaluationLogPage = experimentService.getNextPage(pageRequestDto);
        assertThat(evaluationLogPage).isNotNull();
        assertThat(evaluationLogPage.getTotalElements()).isOne();
    }

    /**
     * Tests global filtering by requests status field with empty data result.
     */
    @Test
    public void testGlobalFilterByRequestStatusWithEmptyData() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW);
        Experiment experiment1 = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        Experiment experiment2 = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        experimentRepository.saveAll(Arrays.asList(experiment, experiment1, experiment2));
        PageRequestDto pageRequestDto = new PageRequestDto(PAGE_NUMBER, PAGE_SIZE, Experiment_.CREATION_DATE, false,
                "query", newArrayList());
        when(filterService.getGlobalFilterFields(FilterTemplateType.EXPERIMENT)).thenReturn(
                Collections.singletonList(Experiment_.EXPERIMENT_STATUS));
        Page<Experiment> evaluationLogPage = experimentService.getNextPage(pageRequestDto);
        assertThat(evaluationLogPage).isNotNull();
        assertThat(evaluationLogPage).isEmpty();
    }

    /**
     * Test filter by experiment type and experiment status order by creation date.
     */
    @Test
    public void testExperimentsFilterByTypeAndStatusOrderByCreationDate() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW);
        experiment.setExperimentType(ExperimentType.ADA_BOOST);
        experiment.setExperimentStatus(RequestStatus.NEW);
        experiment.setCreationDate(LocalDateTime.of(2018, 2, 1, 0, 0, 0));
        experimentRepository.save(experiment);
        Experiment experiment1 = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW);
        experiment1.setExperimentType(ExperimentType.ADA_BOOST);
        experiment1.setExperimentStatus(RequestStatus.NEW);
        experiment1.setCreationDate(LocalDateTime.of(2018, 3, 1, 0, 0, 0));
        experimentRepository.save(experiment1);
        Experiment experiment2 = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW);
        experiment2.setExperimentType(ExperimentType.ADA_BOOST);
        experiment2.setExperimentStatus(RequestStatus.FINISHED);
        experiment2.setCreationDate(LocalDateTime.of(2018, 1, 1, 12, 0, 0));
        experimentRepository.save(experiment2);
        Experiment experiment3 = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW);
        experiment3.setExperimentType(ExperimentType.KNN);
        experiment3.setExperimentStatus(RequestStatus.NEW);
        experiment3.setCreationDate(LocalDateTime.of(2018, 1, 1, 9, 0, 0));
        experimentRepository.save(experiment3);
        PageRequestDto pageRequestDto =
                new PageRequestDto(PAGE_NUMBER, PAGE_SIZE, Experiment_.CREATION_DATE, false, null, newArrayList());
        pageRequestDto.getFilters().add(
                new FilterRequestDto(Experiment_.EXPERIMENT_STATUS, Collections.singletonList(RequestStatus.NEW.name()),
                        MatchMode.EQUALS));
        pageRequestDto.getFilters().add(new FilterRequestDto(Experiment_.EXPERIMENT_TYPE,
                Collections.singletonList(ExperimentType.ADA_BOOST.name()), MatchMode.EQUALS));
        Page<Experiment> experiments = experimentService.getNextPage(pageRequestDto);
        List<Experiment> experimentList = experiments.getContent();
        assertThat(experiments).isNotNull();
        assertThat(experiments.getTotalElements()).isEqualTo(2);
        assertThat(experimentList.size()).isEqualTo(2);
        assertThat(experimentList.get(0).getUuid()).isEqualTo(experiment1.getUuid());
        assertThat(experimentList.get(1).getUuid()).isEqualTo(experiment.getUuid());
    }

    /**
     * Test filter by creation date between order by creation date.
     */
    @Test
    public void testExperimentsFilterByCreationDateBetween() {
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
        assertThat(experimentList.get(0).getUuid()).isEqualTo(experiment1.getUuid());
        assertThat(experimentList.get(1).getUuid()).isEqualTo(experiment.getUuid());
    }

    /**
     * Test filter by creation date between order by creation date.
     * Case 1: Two days interval
     * Case 2: One day interval
     */
    @Test
    public void testExperimentFilterByCreationDateSmallInterval() {
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
    public void testGetExperimentTypesStatistics() {
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
        Assertions.assertThat(experimentTypesMap.size()).isEqualTo(ExperimentType.values().length);
        Assertions.assertThat(experimentTypesMap.get(ExperimentType.ADA_BOOST)).isEqualTo(2L);
        Assertions.assertThat(experimentTypesMap.get(ExperimentType.KNN)).isOne();
        Assertions.assertThat(experimentTypesMap.get(ExperimentType.DECISION_TREE)).isZero();
    }
}
