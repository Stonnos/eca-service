package com.ecaservice.service.experiment.visitor;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.config.CrossValidationConfig;
import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.mapping.EvaluationMethodMapper;
import com.ecaservice.mapping.EvaluationMethodMapperImpl;
import com.ecaservice.model.evaluation.EvaluationMethod;
import com.ecaservice.model.experiment.ExperimentType;
import com.ecaservice.model.experiment.InitializationParams;
import com.ecaservice.service.experiment.ClassifiersSetSearcher;
import eca.dataminer.AbstractExperiment;
import eca.dataminer.AutomatedDecisionTree;
import eca.dataminer.AutomatedHeterogeneousEnsemble;
import eca.dataminer.AutomatedKNearestNeighbours;
import eca.dataminer.AutomatedNeuralNetwork;
import eca.dataminer.AutomatedRandomForests;
import eca.dataminer.AutomatedStacking;
import eca.ensemble.ClassifiersSet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import weka.core.Instances;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.when;

/**
 * Unit tests that checks ExperimentInitializationVisitor functionality {@see ExperimentInitializationVisitor}.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@EnableConfigurationProperties
@TestPropertySource("classpath:application.properties")
@Import({ExperimentConfig.class, CrossValidationConfig.class, EvaluationMethodMapperImpl.class})
public class ExperimentInitializationVisitorTest {

    @Inject
    private ExperimentConfig experimentConfig;
    @Inject
    private CrossValidationConfig crossValidationConfig;
    @Inject
    private EvaluationMethodMapper evaluationMethodMapper;
    @Mock
    private ClassifiersSetSearcher classifiersSetSearcher;

    private ExperimentInitializationVisitor experimentInitializationVisitor;

    @Before
    public void setUp() {
        experimentInitializationVisitor = new ExperimentInitializationVisitor(experimentConfig,
                crossValidationConfig, evaluationMethodMapper, classifiersSetSearcher);
        when(classifiersSetSearcher.findBestClassifiers(any(Instances.class), any(EvaluationMethod.class),
                anyMap())).thenReturn(new ClassifiersSet());
    }

    @Test
    public void testInitializeNeuralNetwork() throws Exception {
        InitializationParams initializationParams = TestHelperUtils.createInitializationParams();
        AutomatedNeuralNetwork automatedNeuralNetwork =
                (AutomatedNeuralNetwork) experimentInitializationVisitor.caseNeuralNetwork(initializationParams);
        assertExperiment(automatedNeuralNetwork);
        assertThat(automatedNeuralNetwork.getNumIterations()).isEqualTo(experimentConfig.getNumIterations().intValue());
        assertThat(automatedNeuralNetwork.getClassifier().getDecimalFormat().getMaximumFractionDigits()).isEqualTo(
                experimentConfig.getMaximumFractionDigits().intValue());
    }

    @Test
    public void testInitializeHeterogeneousEnsemble() throws Exception {
        InitializationParams initializationParams = TestHelperUtils.createInitializationParams();
        AutomatedHeterogeneousEnsemble automatedHeterogeneousEnsemble =
                (AutomatedHeterogeneousEnsemble) experimentInitializationVisitor.caseHeterogeneousEnsemble(
                        initializationParams);
        assertHeterogeneousEnsembleExperiment(automatedHeterogeneousEnsemble);
    }

    @Test
    public void testInitializeModifiedHeterogeneousEnsemble() throws Exception {
        InitializationParams initializationParams = TestHelperUtils.createInitializationParams();
        AutomatedHeterogeneousEnsemble automatedHeterogeneousEnsemble =
                (AutomatedHeterogeneousEnsemble) experimentInitializationVisitor.caseModifiedHeterogeneousEnsemble(
                        initializationParams);
        assertHeterogeneousEnsembleExperiment(automatedHeterogeneousEnsemble);
    }

    @Test
    public void testInitializeAdaBoost() throws Exception {
        InitializationParams initializationParams = TestHelperUtils.createInitializationParams();
        AutomatedHeterogeneousEnsemble automatedHeterogeneousEnsemble =
                (AutomatedHeterogeneousEnsemble) experimentInitializationVisitor.caseAdaBoost(initializationParams);
        assertHeterogeneousEnsembleExperiment(automatedHeterogeneousEnsemble);
    }

    @Test
    public void testInitializeStacking() throws Exception {
        InitializationParams initializationParams = TestHelperUtils.createInitializationParams();
        AutomatedStacking automatedStacking =
                (AutomatedStacking) experimentInitializationVisitor.caseStacking(initializationParams);
        assertExperiment(automatedStacking);
        assertThat(automatedStacking.getClassifier().getClassifiers()).isNotNull();
        assertThat(automatedStacking.getClassifier().getUseCrossValidation()).isFalse();
    }

    @Test
    public void testInitializeKNearestNeighbours() throws Exception {
        InitializationParams initializationParams = TestHelperUtils.createInitializationParams();
        AutomatedKNearestNeighbours automatedKNearestNeighbours =
                (AutomatedKNearestNeighbours) experimentInitializationVisitor.caseKNearestNeighbours(
                        initializationParams);
        assertExperiment(automatedKNearestNeighbours);
        assertThat(automatedKNearestNeighbours.getNumIterations()).isEqualTo(
                experimentConfig.getNumIterations().intValue());
        assertThat(automatedKNearestNeighbours.getClassifier().getDecimalFormat().getMaximumFractionDigits()).isEqualTo(
                experimentConfig.getMaximumFractionDigits().intValue());
    }

    @Test
    public void testInitializeRandomForests() throws Exception {
        InitializationParams initializationParams = TestHelperUtils.createInitializationParams();
        AutomatedRandomForests automatedRandomForests =
                (AutomatedRandomForests) experimentInitializationVisitor.caseRandomForests(
                        initializationParams);
        assertExperiment(automatedRandomForests);
        assertThat(automatedRandomForests.getNumIterations()).isEqualTo(
                experimentConfig.getNumIterations().intValue());
        assertThat(automatedRandomForests.getNumThreads()).isNull();
    }

    @Test
    public void testInitializeStackingCV() throws Exception {
        InitializationParams initializationParams = TestHelperUtils.createInitializationParams();
        AutomatedStacking automatedStacking =
                (AutomatedStacking) experimentInitializationVisitor.caseStackingCV(initializationParams);
        assertExperiment(automatedStacking);
        assertThat(automatedStacking.getClassifier().getClassifiers()).isNotNull();
        assertThat(automatedStacking.getClassifier().getUseCrossValidation()).isTrue();
        assertThat(automatedStacking.getClassifier().getNumFolds()).isEqualTo(
                experimentConfig.getEnsemble().getNumFoldsForStacking());
    }

    @Test
    public void testInitializeDecisionTree() throws Exception {
        InitializationParams initializationParams = TestHelperUtils.createInitializationParams();
        AutomatedDecisionTree automatedDecisionTree =
                (AutomatedDecisionTree) experimentInitializationVisitor.caseDecisionTree(
                        initializationParams);
        assertExperiment(automatedDecisionTree);
        assertThat(automatedDecisionTree.getNumIterations()).isEqualTo(
                experimentConfig.getNumIterations().intValue());
    }

    @Test
    public void testAfterHandle() throws Exception {
        InitializationParams initializationParams = TestHelperUtils.createInitializationParams();
        initializationParams.setEvaluationMethod(EvaluationMethod.CROSS_VALIDATION);
        AbstractExperiment experiment =
                ExperimentType.KNN.handle(experimentInitializationVisitor, initializationParams);
        assertThat(experiment.getEvaluationMethod()).isEqualTo(eca.core.evaluation.EvaluationMethod.CROSS_VALIDATION);
        assertThat(experiment.getNumFolds()).isEqualTo(crossValidationConfig.getNumFolds().intValue());
        assertThat(experiment.getNumTests()).isEqualTo(crossValidationConfig.getNumTests().intValue());
        assertThat(experiment.getSeed()).isEqualTo(crossValidationConfig.getSeed().intValue());
    }

    private void assertHeterogeneousEnsembleExperiment(AutomatedHeterogeneousEnsemble automatedHeterogeneousEnsemble) {
        assertExperiment(automatedHeterogeneousEnsemble);
        assertThat(automatedHeterogeneousEnsemble.getClassifier().getNumIterations()).isEqualTo(
                experimentConfig.getEnsemble().getNumIterations().intValue());
        assertThat(automatedHeterogeneousEnsemble.getClassifier().getClassifiersSet()).isNotNull();
    }

    private void assertExperiment(AbstractExperiment abstractExperiment) {
        assertThat(abstractExperiment).isNotNull();
        assertThat(abstractExperiment.getData()).isNotNull();
        assertThat(abstractExperiment.getClassifier()).isNotNull();
    }
}
