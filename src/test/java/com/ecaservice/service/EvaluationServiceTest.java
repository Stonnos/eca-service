package com.ecaservice.service;

import com.ecaservice.config.CrossValidationConfig;
import com.ecaservice.model.ClassificationResult;
import com.ecaservice.model.EvaluationMethod;
import com.ecaservice.service.impl.EvaluationServiceImpl;
import eca.generators.SimpleDataGenerator;
import eca.metrics.KNearestNeighbours;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;
import weka.core.Instances;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
public class EvaluationServiceTest {

    private static final int SEED = 3;
    private static final int NUM_FOLDS = 10;
    private static final int NUM_TEST = 10;
    private static final int NUM_INSTANCES = 25;
    private static final int NUM_ATTRIBUTES = 6;

    @Mock
    private CrossValidationConfig config;

    private EvaluationService evaluationService;
    private Instances testInstances;

    @Before
    public void setUp() {
        evaluationService = new EvaluationServiceImpl(config);
        when(config.getSeed()).thenReturn(SEED);
        when(config.getNumFolds()).thenReturn(NUM_FOLDS);
        when(config.getNumTests()).thenReturn(NUM_TEST);
        SimpleDataGenerator dataGenerator = new SimpleDataGenerator();
        dataGenerator.setNumInstances(NUM_INSTANCES);
        dataGenerator.setNumAttributes(NUM_ATTRIBUTES);
        testInstances = dataGenerator.generate();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testForNullClassifier() {
        evaluationService.evaluateModel(null, testInstances,
                EvaluationMethod.TRAINING_DATA, NUM_FOLDS, NUM_TEST);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testForNullData() {
        evaluationService.evaluateModel(new KNearestNeighbours(), null,
                EvaluationMethod.TRAINING_DATA, NUM_FOLDS, NUM_TEST);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testForMinimumNumberOfFoldsExceeded() {
        evaluationService.evaluateModel(new KNearestNeighbours(), testInstances,
                EvaluationMethod.CROSS_VALIDATION,
                EvaluationServiceImpl.MINIMUM_NUMBER_OF_FOLDS - 1, NUM_TEST);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testForMinimumNumberOfTestsExceeded() {
        evaluationService.evaluateModel(new KNearestNeighbours(), testInstances,
                EvaluationMethod.CROSS_VALIDATION, NUM_FOLDS,
                EvaluationServiceImpl.MINIMUM_NUMBER_OF_TESTS - 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testForMaximumNumberOfFoldsExceeded() {
        evaluationService.evaluateModel(new KNearestNeighbours(), testInstances,
                EvaluationMethod.CROSS_VALIDATION,
                EvaluationServiceImpl.MAXIMUM_NUMBER_OF_FOLDS + 1, NUM_TEST);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testForMaximumNumberOfTestsExceeded() {
        evaluationService.evaluateModel(new KNearestNeighbours(), testInstances,
                EvaluationMethod.CROSS_VALIDATION, NUM_FOLDS,
                EvaluationServiceImpl.MAXIMUM_NUMBER_OF_TESTS + 1);
    }

    @Test
    public void testTrainingDataMethod() {
        ClassificationResult result = evaluationService.evaluateModel(new KNearestNeighbours(), testInstances,
                EvaluationMethod.TRAINING_DATA, null, null);

        assertTrue(result.isSuccess());
        assertFalse(result.getClassifierDescriptor().getEvaluation().isKCrossValidationMethod());
    }

    @Test
    public void testCrossValidationMethod() {
        ClassificationResult result = evaluationService.evaluateModel(new KNearestNeighbours(), testInstances,
                EvaluationMethod.CROSS_VALIDATION, NUM_FOLDS, NUM_TEST);

        assertTrue(result.isSuccess());
        assertTrue(result.getClassifierDescriptor().getEvaluation().isKCrossValidationMethod());
    }

    @Test
    public void testClassificationResultWithError() {
        when(config.getSeed()).thenThrow(Exception.class);

        ClassificationResult result = evaluationService.evaluateModel(new KNearestNeighbours(), testInstances,
                EvaluationMethod.CROSS_VALIDATION, NUM_FOLDS, NUM_TEST);

        assertFalse(result.isSuccess());
    }

}
