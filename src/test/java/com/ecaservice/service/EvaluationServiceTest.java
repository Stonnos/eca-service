package com.ecaservice.service;

import com.ecaservice.TestDataHelper;
import com.ecaservice.config.CrossValidationConfig;
import com.ecaservice.model.InputData;
import com.ecaservice.model.evaluation.ClassificationResult;
import com.ecaservice.model.evaluation.EvaluationMethod;
import com.ecaservice.model.evaluation.EvaluationOption;
import com.ecaservice.service.evaluation.EvaluationService;
import eca.generators.SimpleDataGenerator;
import eca.metrics.KNearestNeighbours;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;
import weka.core.Instances;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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

    @Mock
    private CrossValidationConfig config;

    private EvaluationService evaluationService;
    private Instances testInstances;

    @Before
    public void setUp() {
        when(config.getSeed()).thenReturn(TestDataHelper.SEED);
        when(config.getNumFolds()).thenReturn(TestDataHelper.NUM_FOLDS);
        when(config.getNumTests()).thenReturn(TestDataHelper.NUM_TESTS);
        SimpleDataGenerator dataGenerator = new SimpleDataGenerator();
        dataGenerator.setNumInstances(TestDataHelper.NUM_INSTANCES);
        dataGenerator.setNumAttributes(TestDataHelper.NUM_ATTRIBUTES);
        testInstances = dataGenerator.generate();
        evaluationService = new EvaluationService(config);
    }

    @Test
    public void testForNullClassifier() {
        InputData inputData = new InputData(null, testInstances);
        ClassificationResult result = evaluationService.evaluateModel(inputData,
                EvaluationMethod.TRAINING_DATA, createEvaluationOptionsMap(TestDataHelper.NUM_FOLDS, TestDataHelper.NUM_TESTS));
        assertFalse(result.isSuccess());
    }

    @Test
    public void testForNullData() {
        InputData inputData = new InputData(new KNearestNeighbours(), null);
        ClassificationResult result = evaluationService.evaluateModel(inputData,
                EvaluationMethod.TRAINING_DATA, createEvaluationOptionsMap(TestDataHelper.NUM_FOLDS,
                        TestDataHelper.NUM_TESTS));
        assertFalse(result.isSuccess());
    }

    @Test
    public void testTrainingDataMethod() {
        InputData inputData = new InputData(new KNearestNeighbours(), testInstances);
        ClassificationResult result = evaluationService.evaluateModel(inputData,
                EvaluationMethod.TRAINING_DATA, Collections.emptyMap());
        assertTrue(result.isSuccess());
        assertFalse(result.getEvaluationResults().getEvaluation().isKCrossValidationMethod());
    }

    @Test
    public void testCrossValidationMethod() {
        InputData inputData = new InputData(new KNearestNeighbours(), testInstances);
        ClassificationResult result = evaluationService.evaluateModel(inputData, EvaluationMethod.CROSS_VALIDATION,
                createEvaluationOptionsMap(TestDataHelper.NUM_FOLDS, TestDataHelper.NUM_TESTS));
        assertTrue(result.isSuccess());
        assertTrue(result.getEvaluationResults().getEvaluation().isKCrossValidationMethod());
    }

    @Test
    public void testClassificationResultWithError() {
        when(config.getSeed()).thenThrow(Exception.class);
        InputData inputData = new InputData(new KNearestNeighbours(), testInstances);
        ClassificationResult result = evaluationService.evaluateModel(inputData, EvaluationMethod.CROSS_VALIDATION,
                createEvaluationOptionsMap(TestDataHelper.NUM_FOLDS, TestDataHelper.NUM_TESTS));
        assertFalse(result.isSuccess());
    }

    private Map<EvaluationOption, String> createEvaluationOptionsMap(int numFolds, int numTests) {
        Map<EvaluationOption, String> evaluationOptionsMap = new HashMap<>();
        evaluationOptionsMap.put(EvaluationOption.NUM_FOLDS, String.valueOf(numFolds));
        evaluationOptionsMap.put(EvaluationOption.NUM_TESTS, String.valueOf(numTests));
        return evaluationOptionsMap;
    }

}
