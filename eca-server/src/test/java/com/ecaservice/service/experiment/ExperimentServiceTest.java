package com.ecaservice.service.experiment;

import com.ecaservice.AssertionUtils;
import com.ecaservice.TestHelperUtils;
import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.dto.ExperimentRequest;
import com.ecaservice.mapping.ExperimentMapper;
import com.ecaservice.mapping.ExperimentMapperImpl;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.RequestStatus;
import com.ecaservice.model.experiment.ExperimentType;
import com.ecaservice.model.experiment.InitializationParams;
import com.ecaservice.repository.ExperimentRepository;
import com.ecaservice.service.AbstractJpaTest;
import com.ecaservice.service.evaluation.CalculationExecutorService;
import com.ecaservice.service.evaluation.CalculationExecutorServiceImpl;
import com.ecaservice.web.dto.model.FilterRequestDto;
import com.ecaservice.web.dto.model.FilterType;
import com.ecaservice.web.dto.model.MatchMode;
import com.ecaservice.web.dto.model.PageRequestDto;
import eca.converters.model.ExperimentHistory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import weka.core.Instances;

import javax.inject.Inject;
import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;


/**
 * Unit tests that checks ExperimentService functionality {@see ExperimentService}.
 *
 * @author Roman Batygin
 */
@Import({ExperimentMapperImpl.class, ExperimentConfig.class})
public class ExperimentServiceTest extends AbstractJpaTest {

    @Inject
    private ExperimentRepository experimentRepository;
    @Inject
    private ExperimentMapper experimentMapper;
    @Mock
    private DataService dataService;
    @Inject
    private ExperimentConfig experimentConfig;
    @Mock
    private ExperimentProcessorService experimentProcessorService;

    private ExperimentService experimentService;

    private Instances data;

    @Before
    public void setUp() throws Exception {
        experimentRepository.deleteAll();
        data = TestHelperUtils.loadInstances();
        CalculationExecutorService executorService =
                new CalculationExecutorServiceImpl(Executors.newCachedThreadPool());
        experimentService = new ExperimentService(experimentRepository, executorService, experimentMapper,
                dataService, experimentConfig, experimentProcessorService);
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
        AssertionUtils.assertSingletonList(experiments);
        Experiment experiment = experiments.get(0);
        assertThat(experiment.getExperimentStatus()).isEqualTo(RequestStatus.NEW);
        assertThat(experiment.getUuid()).isNotNull();
        assertThat(experiment.getCreationDate()).isNotNull();
        assertThat(experiment.getTrainingDataAbsolutePath()).isNotNull();
    }

    @Test
    public void testExperimentRequestCreationWithError() throws Exception {
        ExperimentRequest experimentRequest = TestHelperUtils.createExperimentRequest();
        doThrow(Exception.class).when(dataService).save(any(File.class), any(Instances.class));
        experimentService.createExperiment(experimentRequest);
        List<Experiment> experiments = experimentRepository.findAll();
        AssertionUtils.assertSingletonList(experiments);
        Experiment experiment = experiments.get(0);
        assertThat(experiment.getExperimentStatus()).isEqualTo(RequestStatus.ERROR);
        assertThat(experiment.getCreationDate()).isNotNull();
        assertThat(experiment.getTrainingDataAbsolutePath()).isNull();
    }

    @Test
    public void testFindExperimentFileByUuidWithNullExperiment() {
        assertThat(experimentService.findExperimentFileByUuid(TestHelperUtils.UUID)).isNull();
    }

    @Test
    public void testFindExperimentFileByUuidWithNullFile() {
        Experiment experiment = TestHelperUtils.createExperiment(TestHelperUtils.UUID);
        experiment.setExperimentAbsolutePath(null);
        experimentRepository.save(experiment);
        assertThat(experimentService.findExperimentFileByUuid(TestHelperUtils.UUID)).isNull();
    }

    @Test
    public void testFindExperimentFileByUuidWithErrorStatus() {
        Experiment experiment = TestHelperUtils.createExperiment(TestHelperUtils.UUID, RequestStatus.ERROR);
        experimentRepository.save(experiment);
        assertThat(experimentService.findExperimentFileByUuid(TestHelperUtils.UUID)).isNull();
    }

    @Test
    public void testSuccessFindExperimentFileByUuid() {
        Experiment experiment = TestHelperUtils.createExperiment(TestHelperUtils.UUID, RequestStatus.FINISHED);
        experimentRepository.save(experiment);
        File expectedFile = experimentService.findExperimentFileByUuid(TestHelperUtils.UUID);
        File actualFile = new File(experiment.getExperimentAbsolutePath());
        assertThat(actualFile.getAbsolutePath()).isEqualTo(expectedFile.getAbsolutePath());
    }

    @Test
    public void testProcessExperimentWithSuccessStatus() throws Exception {
        when(dataService.load(any(File.class))).thenReturn(data);
        when(experimentProcessorService.processExperimentHistory(any(Experiment.class),
                any(InitializationParams.class))).thenReturn(new ExperimentHistory());
        doNothing().when(dataService).saveExperimentHistory(any(File.class), any(ExperimentHistory.class));
        experimentService.processExperiment(TestHelperUtils.createExperiment(null));
        List<Experiment> experiments = experimentRepository.findAll();
        AssertionUtils.assertSingletonList(experiments);
        Experiment experiment = experiments.get(0);
        assertThat(experiment.getStartDate()).isNotNull();
        assertThat(experiment.getEndDate()).isNotNull();
        assertThat(experiment.getExperimentAbsolutePath()).isNotNull();
        assertThat(experiment.getExperimentStatus()).isEqualTo(RequestStatus.FINISHED);
    }

    @Test
    public void testProcessExperimentWithErrorStatus() throws Exception {
        when(dataService.load(any(File.class))).thenThrow(new Exception());
        experimentService.processExperiment(TestHelperUtils.createExperiment(null));
        List<Experiment> experiments = experimentRepository.findAll();
        AssertionUtils.assertSingletonList(experiments);
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
        AssertionUtils.assertSingletonList(experiments);
        Experiment experiment = experiments.get(0);
        assertThat(experiment.getStartDate()).isNotNull();
        assertThat(experiment.getEndDate()).isNotNull();
        assertThat(experiment.getExperimentStatus()).isEqualTo(RequestStatus.TIMEOUT);
    }

    @Test
    public void testSuccessRemoveExperiment() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        when(dataService.delete(any(File.class))).thenReturn(true);
        experimentService.removeExperimentData(experiment);
        assertThat(experiment.getExperimentAbsolutePath()).isNull();
        assertThat(experiment.getTrainingDataAbsolutePath()).isNull();
        assertThat(experiment.getDeletedDate()).isNotNull();
    }

    /**
     * Case 1: Experiment training data isn't removed.
     * Case 2: experiment results file isn't removed.
     * Case 3: All files isn't removed.
     */
    @Test
    public void testRemoveExperimentFailed() {
        //Case 1
        Experiment createdExperiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        when(dataService.delete(any(File.class))).thenReturn(false).thenReturn(true);
        experimentService.removeExperimentData(createdExperiment);
        Experiment expectedExperiment = experimentRepository.findById(createdExperiment.getId()).orElse(null);
        assertThat(expectedExperiment).isNotNull();
        assertThat(expectedExperiment.getExperimentAbsolutePath()).isNull();
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
        //Case 3
        createdExperiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        when(dataService.delete(any(File.class))).thenReturn(false).thenReturn(false);
        experimentService.removeExperimentData(createdExperiment);
        expectedExperiment = experimentRepository.findById(createdExperiment.getId()).orElse(null);
        assertThat(expectedExperiment).isNotNull();
        assertThat(expectedExperiment.getExperimentAbsolutePath()).isNotNull();
        assertThat(expectedExperiment.getTrainingDataAbsolutePath()).isNotNull();
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
        PageRequestDto pageRequestDto = new PageRequestDto(0, 10, "creationDate", false, new ArrayList<>());
        pageRequestDto.getFilters().add(
                new FilterRequestDto("experimentStatus", RequestStatus.NEW.name(), FilterType.REFERENCE,
                        MatchMode.EQUALS));
        pageRequestDto.getFilters().add(
                new FilterRequestDto("experimentType", ExperimentType.ADA_BOOST.name(), FilterType.REFERENCE,
                        MatchMode.EQUALS));
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
        PageRequestDto pageRequestDto = new PageRequestDto(0, 10, "creationDate", false, new ArrayList<>());
        pageRequestDto.getFilters().add(
                new FilterRequestDto("creationDate", "2018-01-01 00:00:00", FilterType.DATE, MatchMode.GTE));
        pageRequestDto.getFilters().add(
                new FilterRequestDto("creationDate", "2018-05-01 00:00:00", FilterType.DATE, MatchMode.LTE));
        Page<Experiment> experiments = experimentService.getNextPage(pageRequestDto);
        List<Experiment> experimentList = experiments.getContent();
        assertThat(experiments).isNotNull();
        assertThat(experiments.getTotalElements()).isEqualTo(2);
        assertThat(experimentList.size()).isEqualTo(2);
        assertThat(experimentList.get(0).getUuid()).isEqualTo(experiment1.getUuid());
        assertThat(experimentList.get(1).getUuid()).isEqualTo(experiment.getUuid());
    }
}
