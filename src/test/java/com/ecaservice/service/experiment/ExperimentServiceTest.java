package com.ecaservice.service.experiment;

import com.ecaservice.AssertionUtils;
import com.ecaservice.TestHelperUtils;
import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.mapping.ExperimentMapper;
import com.ecaservice.mapping.ExperimentMapperImpl;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.experiment.ExperimentRequest;
import com.ecaservice.model.experiment.ExperimentStatus;
import com.ecaservice.model.experiment.InitializationParams;
import com.ecaservice.repository.ExperimentRepository;
import com.ecaservice.service.evaluation.CalculationExecutorService;
import com.ecaservice.service.evaluation.impl.CalculationExecutorServiceImpl;
import eca.converters.model.ExperimentHistory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import weka.core.Instances;

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;


/**
 * Unit tests that checks ExperimentService functionality (see {@link ExperimentService}).
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@Import( {ExperimentMapperImpl.class, ExperimentConfig.class})
public class ExperimentServiceTest extends AbstractExperimentTest {

    @Autowired
    private ExperimentRepository experimentRepository;

    @Autowired
    private ExperimentMapper experimentMapper;
    @Mock
    private DataService dataService;
    @Autowired
    private ExperimentConfig experimentConfig;
    @Mock
    private ExperimentProcessorService experimentProcessorService;

    private CalculationExecutorService executorService;

    private ExperimentService experimentService;

    private Instances data;

    @Before
    public void setUp() throws Exception {
        experimentRepository.deleteAll();
        data = TestHelperUtils.loadInstances();
        executorService = new CalculationExecutorServiceImpl(Executors.newCachedThreadPool());
        experimentService = new ExperimentService(experimentRepository, executorService, experimentMapper,
                dataService, experimentConfig, experimentProcessorService);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullExperimentRequest() {
        experimentService.createExperiment(null);
    }

    @Test
    public void testSuccessExperimentRequestCreation() throws Exception {
        ExperimentRequest experimentRequest = TestHelperUtils.createExperimentRequest();
        doNothing().when(dataService).save(any(File.class), any(Instances.class));
        experimentService.createExperiment(experimentRequest);
        List<Experiment> experiments = experimentRepository.findAll();
        AssertionUtils.assertList(experiments);
        Experiment experiment = experiments.get(0);
        assertEquals(experiment.getExperimentStatus(), ExperimentStatus.NEW);
        assertNotNull(experiment.getCreationDate());
        assertNotNull(experiment.getTrainingDataAbsolutePath());
    }

    @Test
    public void testExperimentRequestCreationWithError() throws Exception {
        ExperimentRequest experimentRequest = TestHelperUtils.createExperimentRequest();
        doThrow(Exception.class).when(dataService).save(any(File.class), any(Instances.class));
        experimentService.createExperiment(experimentRequest);
        List<Experiment> experiments = experimentRepository.findAll();
        AssertionUtils.assertList(experiments);
        Experiment experiment = experiments.get(0);
        assertEquals(experiment.getExperimentStatus(), ExperimentStatus.ERROR);
        assertNotNull(experiment.getCreationDate());
        assertNull(experiment.getTrainingDataAbsolutePath());
    }

    @Test
    public void testFindExperimentFileByUuidWithNullExperiment() {
        assertNull(experimentService.findExperimentFileByUuid(TestHelperUtils.UUID));
    }

    @Test
    public void testFindExperimentFileByUuidWithNullFile() {
        Experiment experiment = TestHelperUtils.createExperiment(TestHelperUtils.UUID);
        experiment.setExperimentAbsolutePath(null);
        experimentRepository.save(experiment);
        assertNull(experimentService.findExperimentFileByUuid(TestHelperUtils.UUID));
    }

    @Test
    public void testSuccessFindExperimentFileByUuid() {
        Experiment experiment = TestHelperUtils.createExperiment(TestHelperUtils.UUID);
        experimentRepository.save(experiment);
        File actualFile = experimentService.findExperimentFileByUuid(TestHelperUtils.UUID);
        File expectedFile = new File(experiment.getExperimentAbsolutePath());
        assertEquals(actualFile.getAbsolutePath(), expectedFile.getAbsolutePath());
    }

    @Test
    public void testProcessExperimentWithSuccessStatus() throws Exception {
        when(dataService.load(any(File.class))).thenReturn(data);
        when(experimentProcessorService.processExperimentHistory(any(Experiment.class),
                any(InitializationParams.class))).thenReturn(new ExperimentHistory());
        doNothing().when(dataService).save(any(File.class), any(ExperimentHistory.class));
        experimentService.processExperiment(TestHelperUtils.createExperiment(null));
        List<Experiment> experiments = experimentRepository.findAll();
        AssertionUtils.assertList(experiments);
        Experiment experiment = experiments.get(0);
        assertNotNull(experiment.getStartDate());
        assertNotNull(experiment.getEndDate());
        assertNotNull(experiment.getExperimentAbsolutePath());
        assertNotNull(experiment.getUuid());
        assertEquals(experiment.getExperimentStatus(), ExperimentStatus.FINISHED);
    }

    @Test
    public void testProcessExperimentWithErrorStatus() throws Exception {
        when(dataService.load(any(File.class))).thenThrow(Exception.class);
        experimentService.processExperiment(TestHelperUtils.createExperiment(null));
        List<Experiment> experiments = experimentRepository.findAll();
        AssertionUtils.assertList(experiments);
        Experiment experiment = experiments.get(0);
        assertNotNull(experiment.getStartDate());
        assertNotNull(experiment.getEndDate());
        assertEquals(experiment.getExperimentStatus(), ExperimentStatus.ERROR);
    }

    @Test
    public void testProcessExperimentWithTimeoutStatus() throws Exception {
        when(dataService.load(any(File.class))).thenReturn(data);
        when(experimentProcessorService.processExperimentHistory(any(Experiment.class),
                any(InitializationParams.class))).thenReturn(new ExperimentHistory());
        doThrow(TimeoutException.class).when(dataService).save(any(File.class), any(ExperimentHistory.class));
        experimentService.processExperiment(TestHelperUtils.createExperiment(null));
        List<Experiment> experiments = experimentRepository.findAll();
        AssertionUtils.assertList(experiments);
        Experiment experiment = experiments.get(0);
        assertNotNull(experiment.getStartDate());
        assertNotNull(experiment.getEndDate());
        assertEquals(experiment.getExperimentStatus(), ExperimentStatus.TIMEOUT);
    }

    @Test
    public void testRemoveExperiment() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        experimentService.removeExperimentData(experiment);
        assertNull(experiment.getExperimentAbsolutePath());
        assertNull(experiment.getTrainingDataAbsolutePath());
        assertNotNull(experiment.getDeletedDate());
    }

}
