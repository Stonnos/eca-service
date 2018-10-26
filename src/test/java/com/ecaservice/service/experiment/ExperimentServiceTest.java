package com.ecaservice.service.experiment;

import com.ecaservice.AssertionUtils;
import com.ecaservice.TestHelperUtils;
import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.dto.ExperimentRequest;
import com.ecaservice.mapping.ExperimentMapper;
import com.ecaservice.mapping.ExperimentMapperImpl;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.experiment.ExperimentStatus;
import com.ecaservice.model.experiment.InitializationParams;
import com.ecaservice.repository.ExperimentRepository;
import com.ecaservice.service.AbstractJpaTest;
import com.ecaservice.service.evaluation.CalculationExecutorService;
import com.ecaservice.service.evaluation.CalculationExecutorServiceImpl;
import eca.converters.model.ExperimentHistory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.context.annotation.Import;
import weka.core.Instances;

import javax.inject.Inject;
import java.io.File;
import java.util.List;
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
        assertThat(experiment.getExperimentStatus()).isEqualTo(ExperimentStatus.ERROR);
    }

    @Test
    public void testSuccessExperimentRequestCreation() throws Exception {
        ExperimentRequest experimentRequest = TestHelperUtils.createExperimentRequest();
        doNothing().when(dataService).save(any(File.class), any(Instances.class));
        experimentService.createExperiment(experimentRequest);
        List<Experiment> experiments = experimentRepository.findAll();
        AssertionUtils.assertSingletonList(experiments);
        Experiment experiment = experiments.get(0);
        assertThat(experiment.getExperimentStatus()).isEqualTo(ExperimentStatus.NEW);
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
        assertThat(experiment.getExperimentStatus()).isEqualTo(ExperimentStatus.ERROR);
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
        Experiment experiment = TestHelperUtils.createExperiment(TestHelperUtils.UUID, ExperimentStatus.ERROR);
        experimentRepository.save(experiment);
        assertThat(experimentService.findExperimentFileByUuid(TestHelperUtils.UUID)).isNull();
    }

    @Test
    public void testSuccessFindExperimentFileByUuid() {
        Experiment experiment = TestHelperUtils.createExperiment(TestHelperUtils.UUID, ExperimentStatus.FINISHED);
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
        assertThat(experiment.getExperimentStatus()).isEqualTo(ExperimentStatus.FINISHED);
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
        assertThat(experiment.getExperimentStatus()).isEqualTo(ExperimentStatus.ERROR);
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
        assertThat(experiment.getExperimentStatus()).isEqualTo(ExperimentStatus.TIMEOUT);
    }

    @Test
    public void testRemoveExperiment() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        experimentService.removeExperimentData(experiment);
        assertThat(experiment.getExperimentAbsolutePath()).isNull();
        assertThat(experiment.getTrainingDataAbsolutePath()).isNull();
        assertThat(experiment.getDeletedDate()).isNotNull();
    }
}
