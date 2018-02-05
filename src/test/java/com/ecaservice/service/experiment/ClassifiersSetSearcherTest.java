package com.ecaservice.service.experiment;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.exception.ExperimentException;
import com.ecaservice.model.evaluation.ClassificationResult;
import com.ecaservice.model.evaluation.EvaluationMethod;
import com.ecaservice.service.evaluation.EvaluationService;
import com.ecaservice.service.experiment.handler.ClassifierInputDataHandler;
import com.ecaservice.service.experiment.handler.NeuralNetworkInputDataHandler;
import eca.core.evaluation.Evaluation;
import eca.core.evaluation.EvaluationResults;
import eca.ensemble.ClassifiersSet;
import eca.metrics.KNearestNeighbours;
import eca.regression.Logistic;
import eca.trees.C45;
import eca.trees.CART;
import eca.trees.CHAID;
import eca.trees.ID3;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link ClassifiersSetSearcher} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@Import({ExperimentConfig.class, NeuralNetworkInputDataHandler.class})
public class ClassifiersSetSearcherTest extends AbstractExperimentTest {

    @Autowired
    private ExperimentConfig experimentConfig;
    @Autowired
    private List<ClassifierInputDataHandler> classifierInputDataHandlers;
    @Mock
    private EvaluationService evaluationService;
    @Mock
    private ExperimentConfigurationService experimentConfigurationService;

    private ClassifiersSetSearcher classifiersSetSearcher;
    private Instances testInstances;
    private EvaluationResults evaluationResults;

    @Before
    public void init() throws Exception {
        testInstances = TestHelperUtils.loadInstances();
        classifiersSetSearcher = new ClassifiersSetSearcher(evaluationService, experimentConfigurationService,
                experimentConfig, classifierInputDataHandlers);
        evaluationResults = new EvaluationResults(new KNearestNeighbours(), new Evaluation(testInstances));
    }

    @Test(expected = ExperimentException.class)
    public void testEmptySet() {
        ClassificationResult classificationResult = new ClassificationResult();
        when(experimentConfigurationService.findClassifiers()).thenReturn(Arrays.asList(new CART(), new Logistic()));
        when(evaluationService.evaluateModel(anyObject(), anyObject(), anyObject())).thenReturn(classificationResult);
        classifiersSetSearcher.findBestClassifiers(testInstances, EvaluationMethod.TRAINING_DATA,
                Collections.emptyMap());
    }

    /**
     * Unit test that checking success building cases:
     * Case 1: the classifiers size is greater than best classifiers number specified in configs.
     * Case 2: the classifiers size is less than best classifiers number specified in configs.
     */
    @Test
    public void testSuccessBuilt() {
        ClassificationResult classificationResult = new ClassificationResult();
        classificationResult.setSuccess(true);
        classificationResult.setEvaluationResults(evaluationResults);
        //checks case 1
        when(experimentConfigurationService.findClassifiers()).thenReturn(Arrays.asList(new CART(), new Logistic(),
                new ID3(), new C45(), new CHAID()));
        when(evaluationService.evaluateModel(anyObject(), anyObject(), anyObject())).thenReturn(classificationResult);
        ClassifiersSet classifiers = classifiersSetSearcher.findBestClassifiers(testInstances, EvaluationMethod
                .TRAINING_DATA, Collections.emptyMap());
        assertThat(classifiers.size()).isEqualTo(experimentConfig.getEnsemble().getNumBestClassifiers().intValue());
        //checks case 2
        List<AbstractClassifier> classifierList = Arrays.asList(new CART(), new Logistic());
        when(experimentConfigurationService.findClassifiers()).thenReturn(classifierList);
        classifiers = classifiersSetSearcher.findBestClassifiers(testInstances, EvaluationMethod
                .TRAINING_DATA, Collections.emptyMap());
        assertThat(classifierList.size()).isEqualTo(classifiers.size());
    }


}
