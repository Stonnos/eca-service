package com.ecaservice.service.experiment;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.experiment.InitializationParams;
import com.ecaservice.service.experiment.visitor.ExperimentInitializationVisitor;
import eca.converters.model.ExperimentHistory;
import eca.dataminer.AutomatedKNearestNeighbours;
import eca.metrics.KNearestNeighbours;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;
import weka.core.Instances;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
/**
 * Unit tests that checks ExperimentProcessorService functionality (see {@link ExperimentProcessorService}).
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
public class ExperimentProcessorServiceTest {

    private static final int RESULTS_SIZE = 5;

    @Mock
    private ExperimentConfig experimentConfig;
    @Mock
    private ExperimentInitializationVisitor experimentInitializationVisitor;

    @InjectMocks
    private ExperimentProcessorService experimentProcessorService;

    private Instances data;

    @Before
    public void setUp() {
        when(experimentConfig.getResultSize()).thenReturn(RESULTS_SIZE);
        data = TestHelperUtils.generateInstances(TestHelperUtils.NUM_INSTANCES, TestHelperUtils.NUM_ATTRIBUTES);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testNullInitializationParams() {
        experimentProcessorService.processExperimentHistory(new Experiment(), null);
    }

    @Test
    public void testProcessExperimentHistory() {
        InitializationParams initializationParams = TestHelperUtils.createInitializationParams(data);
        AutomatedKNearestNeighbours automatedKNearestNeighbours =
                new AutomatedKNearestNeighbours(data, new KNearestNeighbours());
        when(experimentInitializationVisitor.caseKNearestNeighbours(initializationParams))
                .thenReturn(automatedKNearestNeighbours);
        ExperimentHistory experimentHistory = experimentProcessorService.processExperimentHistory(
                TestHelperUtils.createExperiment(null), initializationParams);
        assertNotNull(experimentHistory);
        assertNotNull(experimentHistory.getExperiment());
        assertEquals(experimentHistory.getExperiment().size(), experimentConfig.getResultSize().intValue());
    }

}
