package com.ecaservice.service;

import com.ecaservice.config.CrossValidationConfig;
import com.ecaservice.dto.ClassificationResult;
import com.ecaservice.model.EvaluationMethod;
import com.ecaservice.service.impl.EvaluationServiceImpl;
import eca.generators.SimpleDataGenerator;
import eca.metrics.KNearestNeighbours;
import eca.model.InputData;
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
 * Unit tests that checks EvaluationService functionality (see {@link EvaluationService}).
 *
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
        when(config.getSeed()).thenReturn(SEED);
        when(config.getNumFolds()).thenReturn(NUM_FOLDS);
        when(config.getNumTests()).thenReturn(NUM_TEST);
        SimpleDataGenerator dataGenerator = new SimpleDataGenerator();
        dataGenerator.setNumInstances(NUM_INSTANCES);
        dataGenerator.setNumAttributes(NUM_ATTRIBUTES);
        testInstances = dataGenerator.generate();
        evaluationService = new EvaluationServiceImpl(config);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testForNullClassifier() {
        InputData inputData = new InputData(null, testInstances);
        evaluationService.evaluateModel(inputData,
                EvaluationMethod.TRAINING_DATA, NUM_FOLDS, NUM_TEST);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testForNullData() {
        InputData inputData = new InputData(new KNearestNeighbours(), null);
        evaluationService.evaluateModel(inputData,
                EvaluationMethod.TRAINING_DATA, NUM_FOLDS, NUM_TEST);
    }


    @Test
    public void testForMinimumNumberOfFoldsExceeded() {
        InputData inputData = new InputData(new KNearestNeighbours(), testInstances);
        ClassificationResult result = evaluationService.evaluateModel(inputData,
                EvaluationMethod.CROSS_VALIDATION,
                EvaluationServiceImpl.MINIMUM_NUMBER_OF_FOLDS - 1, NUM_TEST);
        assertFalse(result.isSuccess());
    }

    @Test
    public void testForMinimumNumberOfTestsExceeded() {
        InputData inputData = new InputData(new KNearestNeighbours(), testInstances);
        ClassificationResult result = evaluationService.evaluateModel(inputData,
                EvaluationMethod.CROSS_VALIDATION, NUM_FOLDS,
                EvaluationServiceImpl.MINIMUM_NUMBER_OF_TESTS - 1);
        assertFalse(result.isSuccess());
    }

    @Test
    public void testForMaximumNumberOfFoldsExceeded() {
        InputData inputData = new InputData(new KNearestNeighbours(), testInstances);
        ClassificationResult result = evaluationService.evaluateModel(inputData,
                EvaluationMethod.CROSS_VALIDATION,
                EvaluationServiceImpl.MAXIMUM_NUMBER_OF_FOLDS + 1, NUM_TEST);
        assertFalse(result.isSuccess());
    }

    @Test
    public void testForMaximumNumberOfTestsExceeded() {
        InputData inputData = new InputData(new KNearestNeighbours(), testInstances);
        ClassificationResult result = evaluationService.evaluateModel(inputData,
                EvaluationMethod.CROSS_VALIDATION, NUM_FOLDS,
                EvaluationServiceImpl.MAXIMUM_NUMBER_OF_TESTS + 1);
        assertFalse(result.isSuccess());
    }

    @Test
    public void testTrainingDataMethod() {
        InputData inputData = new InputData(new KNearestNeighbours(), testInstances);
        ClassificationResult result = evaluationService.evaluateModel(inputData,
                EvaluationMethod.TRAINING_DATA, null, null);

        assertTrue(result.isSuccess());
        assertFalse(result.getClassifierDescriptor().getEvaluation().isKCrossValidationMethod());
    }

    @Test
    public void testCrossValidationMethod() {
        InputData inputData = new InputData(new KNearestNeighbours(), testInstances);
        ClassificationResult result = evaluationService.evaluateModel(inputData,
                EvaluationMethod.CROSS_VALIDATION, NUM_FOLDS, NUM_TEST);

        assertTrue(result.isSuccess());
        assertTrue(result.getClassifierDescriptor().getEvaluation().isKCrossValidationMethod());
    }

    @Test
    public void testClassificationResultWithError() {
        when(config.getSeed()).thenThrow(Exception.class);

        InputData inputData = new InputData(new KNearestNeighbours(), testInstances);
        ClassificationResult result = evaluationService.evaluateModel(inputData,
                EvaluationMethod.CROSS_VALIDATION, NUM_FOLDS, NUM_TEST);

        assertFalse(result.isSuccess());
    }

}
