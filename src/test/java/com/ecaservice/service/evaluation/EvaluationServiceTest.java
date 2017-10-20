package com.ecaservice.service.evaluation;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.config.CrossValidationConfig;
import com.ecaservice.model.InputData;
import com.ecaservice.model.evaluation.ClassificationResult;
import com.ecaservice.model.evaluation.EvaluationMethod;
import eca.generators.SimpleDataGenerator;
import eca.metrics.KNearestNeighbours;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;
import weka.core.Instances;

import java.util.Collections;

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
        when(config.getSeed()).thenReturn(TestHelperUtils.SEED);
        when(config.getNumFolds()).thenReturn(TestHelperUtils.NUM_FOLDS);
        when(config.getNumTests()).thenReturn(TestHelperUtils.NUM_TESTS);
        SimpleDataGenerator dataGenerator = new SimpleDataGenerator();
        dataGenerator.setNumInstances(TestHelperUtils.NUM_INSTANCES);
        dataGenerator.setNumAttributes(TestHelperUtils.NUM_ATTRIBUTES);
        testInstances = dataGenerator.generate();
        evaluationService = new EvaluationService(config);
    }

    @Test
    public void testForNullInputData() {
        ClassificationResult result = evaluationService.evaluateModel(null, EvaluationMethod.TRAINING_DATA,
                TestHelperUtils.createEvaluationOptionsMap(TestHelperUtils.NUM_FOLDS, TestHelperUtils.NUM_TESTS));
        assertFalse(result.isSuccess());
    }

    @Test
    public void testForNullClassifier() {
        InputData inputData = new InputData(null, testInstances);
        ClassificationResult result = evaluationService.evaluateModel(inputData, EvaluationMethod.TRAINING_DATA,
                TestHelperUtils.createEvaluationOptionsMap(TestHelperUtils.NUM_FOLDS, TestHelperUtils.NUM_TESTS));
        assertFalse(result.isSuccess());
    }

    @Test
    public void testForNullData() {
        InputData inputData = new InputData(new KNearestNeighbours(), null);
        ClassificationResult result = evaluationService.evaluateModel(inputData,
                EvaluationMethod.TRAINING_DATA, TestHelperUtils.createEvaluationOptionsMap(TestHelperUtils.NUM_FOLDS,
                        TestHelperUtils.NUM_TESTS));
        assertFalse(result.isSuccess());
    }

    @Test
    public void testForNullEvaluationMethod() {
        InputData inputData = new InputData(new KNearestNeighbours(), testInstances);
        ClassificationResult result = evaluationService.evaluateModel(inputData, null,
                TestHelperUtils.createEvaluationOptionsMap(TestHelperUtils.NUM_FOLDS, TestHelperUtils.NUM_TESTS));
        assertFalse(result.isSuccess());
    }

    @Test
    public void testForNullEvaluationOptionsMap() {
        InputData inputData = new InputData(new KNearestNeighbours(), testInstances);
        ClassificationResult result =
                evaluationService.evaluateModel(inputData, EvaluationMethod.TRAINING_DATA, null);
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
                TestHelperUtils.createEvaluationOptionsMap(TestHelperUtils.NUM_FOLDS, TestHelperUtils.NUM_TESTS));
        assertTrue(result.isSuccess());
        assertTrue(result.getEvaluationResults().getEvaluation().isKCrossValidationMethod());
    }

    @Test
    public void testClassificationResultWithError() {
        when(config.getSeed()).thenThrow(Exception.class);
        InputData inputData = new InputData(new KNearestNeighbours(), testInstances);
        ClassificationResult result = evaluationService.evaluateModel(inputData, EvaluationMethod.CROSS_VALIDATION,
                TestHelperUtils.createEvaluationOptionsMap(TestHelperUtils.NUM_FOLDS, TestHelperUtils.NUM_TESTS));
        assertFalse(result.isSuccess());
    }


}
