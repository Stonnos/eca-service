package com.ecaservice.service.experiment.visitor;

import com.ecaservice.TestDataHelper;
import com.ecaservice.config.CrossValidationConfig;
import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.mapping.EvaluationMethodMapper;
import com.ecaservice.mapping.EvaluationMethodMapperTest;
import com.ecaservice.model.evaluation.EvaluationMethod;
import com.ecaservice.model.experiment.ExperimentType;
import com.ecaservice.model.experiment.InitializationParams;
import eca.dataminer.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Unit tests that checks ExperimentInitializationVisitor functionality (see {@link ExperimentInitializationVisitor}).
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@Import({ExperimentConfig.class, CrossValidationConfig.class, EvaluationMethodMapperTest.class})
@EnableConfigurationProperties
@TestPropertySource("classpath:application-test.properties")
public class ExperimentInitializationVisitorTest {

    @Autowired
    private ExperimentConfig experimentConfig;
    @Autowired
    private CrossValidationConfig crossValidationConfig;
    @Autowired
    private EvaluationMethodMapper evaluationMethodMapper;

    private ExperimentInitializationVisitor experimentInitializationVisitor;

    @Before
    public void setUp() {
        experimentInitializationVisitor = new ExperimentInitializationVisitor(experimentConfig,
                crossValidationConfig, evaluationMethodMapper);
    }

    @Test
    public void testInitializeNeuralNetwork() {
        InitializationParams initializationParams = TestDataHelper.createInitializationParams();
        AutomatedNeuralNetwork automatedNeuralNetwork =
                (AutomatedNeuralNetwork) experimentInitializationVisitor.caseNeuralNetwork(initializationParams);
        assertExperiment(automatedNeuralNetwork);
        assertEquals(experimentConfig.getNumIterations().intValue(), automatedNeuralNetwork.getNumIterations());
        assertEquals(experimentConfig.getMaximumFractionDigits().intValue(),
                automatedNeuralNetwork.getClassifier().getDecimalFormat().getMaximumFractionDigits());
    }

    @Test
    public void testInitializeHeterogeneousEnsemble() {
        InitializationParams initializationParams = TestDataHelper.createInitializationParams();
        AutomatedHeterogeneousEnsemble automatedHeterogeneousEnsemble =
                (AutomatedHeterogeneousEnsemble) experimentInitializationVisitor.caseHeterogeneousEnsemble(initializationParams);
        assertHeterogeneousEnsembleExperiment(automatedHeterogeneousEnsemble);
    }

    @Test
    public void testInitializeModifiedHeterogeneousEnsemble() {
        InitializationParams initializationParams = TestDataHelper.createInitializationParams();
        AutomatedHeterogeneousEnsemble automatedHeterogeneousEnsemble =
                (AutomatedHeterogeneousEnsemble) experimentInitializationVisitor.caseModifiedHeterogeneousEnsemble(initializationParams);
        assertHeterogeneousEnsembleExperiment(automatedHeterogeneousEnsemble);
    }

    @Test
    public void testInitializeAdaBoost() {
        InitializationParams initializationParams = TestDataHelper.createInitializationParams();
        AutomatedHeterogeneousEnsemble automatedHeterogeneousEnsemble =
                (AutomatedHeterogeneousEnsemble) experimentInitializationVisitor.caseAdaBoost(initializationParams);
        assertHeterogeneousEnsembleExperiment(automatedHeterogeneousEnsemble);
    }

    @Test
    public void testInitializeStacking() {
        InitializationParams initializationParams = TestDataHelper.createInitializationParams();
        AutomatedStacking automatedStacking =
                (AutomatedStacking) experimentInitializationVisitor.caseStacking(initializationParams);
        assertExperiment(automatedStacking);
        assertNotNull(automatedStacking.getClassifier().getClassifiers());
    }

    @Test
    public void testInitializeKNearestNeighbours() {
        InitializationParams initializationParams = TestDataHelper.createInitializationParams();
        AutomatedKNearestNeighbours automatedKNearestNeighbours =
                (AutomatedKNearestNeighbours) experimentInitializationVisitor.caseKNearestNeighbours(initializationParams);
        assertExperiment(automatedKNearestNeighbours);
        assertEquals(experimentConfig.getNumIterations().intValue(), automatedKNearestNeighbours.getNumIterations());
        assertEquals(experimentConfig.getMaximumFractionDigits().intValue(),
                automatedKNearestNeighbours.getClassifier().getDecimalFormat().getMaximumFractionDigits());
    }

    @Test
    public void testAfterHandle() {
        InitializationParams initializationParams = TestDataHelper.createInitializationParams();
        initializationParams.setEvaluationMethod(EvaluationMethod.CROSS_VALIDATION);
        AbstractExperiment experiment = ExperimentType.KNN.handle(experimentInitializationVisitor, initializationParams);
        assertEquals(eca.core.evaluation.EvaluationMethod.CROSS_VALIDATION, experiment.getEvaluationMethod());
        assertEquals(crossValidationConfig.getNumFolds().intValue(), experiment.getNumFolds());
        assertEquals(crossValidationConfig.getNumTests().intValue(), experiment.getNumTests());

    }

    private void assertHeterogeneousEnsembleExperiment(AutomatedHeterogeneousEnsemble automatedHeterogeneousEnsemble) {
        assertExperiment(automatedHeterogeneousEnsemble);
        assertEquals(experimentConfig.getEnsemble().getNumIterations().intValue(),
                automatedHeterogeneousEnsemble.getClassifier().getIterationsNum());
        assertNotNull(automatedHeterogeneousEnsemble.getClassifier().getClassifiersSet());
    }

    private void assertExperiment(AbstractExperiment abstractExperiment) {
        assertNotNull(abstractExperiment);
        assertNotNull(abstractExperiment.getData());
        assertNotNull(abstractExperiment.getClassifier());
    }
}
