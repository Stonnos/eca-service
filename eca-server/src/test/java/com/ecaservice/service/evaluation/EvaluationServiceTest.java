package com.ecaservice.service.evaluation;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.config.CrossValidationConfig;
import com.ecaservice.model.InputData;
import com.ecaservice.model.evaluation.ClassificationResult;
import eca.core.evaluation.Evaluation;
import eca.core.evaluation.EvaluationMethod;
import eca.metrics.KNearestNeighbours;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import weka.classifiers.Classifier;
import weka.core.Instances;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doThrow;

/**
 * Unit tests that checks EvaluationService functionality {@see EvaluationService}.
 *
 * @author Roman Batygin
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@PrepareForTest({Evaluation.class, EvaluationService.class})
@EnableConfigurationProperties
@TestPropertySource("classpath:application.properties")
@Import(CrossValidationConfig.class)
public class EvaluationServiceTest {

    @Inject
    private CrossValidationConfig config;

    private EvaluationService evaluationService;
    private Instances testInstances;

    @Before
    public void setUp() {
        testInstances = TestHelperUtils.loadInstances();
        evaluationService = new EvaluationService(config);
    }

    @Test
    public void testTrainingDataMethod() {
        InputData inputData = new InputData(new KNearestNeighbours(), testInstances);
        ClassificationResult result = evaluationService.evaluateModel(inputData,
                EvaluationMethod.TRAINING_DATA, Collections.emptyMap());
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getEvaluationResults().getEvaluation().isKCrossValidationMethod()).isFalse();
    }

    @Test
    public void testCrossValidationMethod() {
        InputData inputData = new InputData(new KNearestNeighbours(), testInstances);
        ClassificationResult result = evaluationService.evaluateModel(inputData, EvaluationMethod.CROSS_VALIDATION,
                TestHelperUtils.createEvaluationOptionsMap(TestHelperUtils.NUM_FOLDS, TestHelperUtils.NUM_TESTS));
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getEvaluationResults().getEvaluation().isKCrossValidationMethod()).isTrue();
    }

    @Test
    public void testClassificationResultWithError() throws Exception {
        Evaluation evaluation = PowerMockito.mock(Evaluation.class);
        PowerMockito.whenNew(Evaluation.class).withAnyArguments().thenReturn(evaluation);
        doThrow(new Exception()).when(evaluation).kCrossValidateModel(any(Classifier.class), any(Instances.class),
                anyInt(), anyInt(), any(Random.class));
        InputData inputData = new InputData(new KNearestNeighbours(), testInstances);
        ClassificationResult result = evaluationService.evaluateModel(inputData, EvaluationMethod.CROSS_VALIDATION,
                TestHelperUtils.createEvaluationOptionsMap(TestHelperUtils.NUM_FOLDS, TestHelperUtils.NUM_TESTS));
        assertThat(result.isSuccess()).isFalse();
    }
}
