package com.ecaservice.server.service.experiment;

import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.config.ExperimentConfig;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.experiment.InitializationParams;
import com.ecaservice.server.service.experiment.visitor.ExperimentInitializationVisitor;
import eca.dataminer.AbstractExperiment;
import eca.dataminer.AutomatedKNearestNeighbours;
import eca.metrics.KNearestNeighbours;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import weka.core.Instances;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * Unit tests that checks ExperimentProcessorService functionality {@see ExperimentProcessorService}.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
class ExperimentProcessorServiceTest {

    private static final int RESULTS_SIZE = 5;

    @Mock
    private ExperimentConfig experimentConfig;
    @Mock
    private ApplicationEventPublisher applicationEventPublisher;
    @Mock
    private ExperimentInitializationVisitor experimentInitializationVisitor;

    @InjectMocks
    private ExperimentProcessorService experimentProcessorService;

    private Instances data;

    @BeforeEach
    void setUp() {
        when(experimentConfig.getResultSize()).thenReturn(RESULTS_SIZE);
        data = TestHelperUtils.loadInstances();
    }

    @Test
    void testNullInitializationParams() {
        Experiment experiment = new Experiment();
        assertThrows(IllegalArgumentException.class,
                () -> experimentProcessorService.processExperimentHistory(experiment, null));
    }

    @Test
    void testProcessExperimentHistory() {
        InitializationParams initializationParams = TestHelperUtils.createInitializationParams(data);
        AutomatedKNearestNeighbours automatedKNearestNeighbours =
                new AutomatedKNearestNeighbours(data, new KNearestNeighbours());
        when(experimentInitializationVisitor.caseKNearestNeighbours(initializationParams))
                .thenReturn(automatedKNearestNeighbours);
        AbstractExperiment<?> experiment = experimentProcessorService.processExperimentHistory(
                TestHelperUtils.createExperiment(null), initializationParams);
        assertThat(experiment).isNotNull();
        assertThat(experiment.getHistory().size()).isEqualTo(experimentConfig.getResultSize().intValue());
    }

}
