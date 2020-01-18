package com.ecaservice.service.evaluation;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.config.CrossValidationConfig;
import com.ecaservice.dto.EvaluationRequest;
import com.ecaservice.model.evaluation.ClassificationResult;
import eca.core.evaluation.Evaluation;
import eca.core.evaluation.EvaluationMethod;
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
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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

    @Before
    public void setUp() {
        evaluationService = new EvaluationService(config);
    }

    @Test
    public void testTrainingDataMethod() {
        EvaluationRequest evaluationRequest = TestHelperUtils.createEvaluationRequest();
        ClassificationResult result = evaluationService.evaluateModel(evaluationRequest);
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getEvaluationResults().getEvaluation().isKCrossValidationMethod()).isFalse();
    }

    @Test
    public void testCrossValidationMethod() {
        EvaluationRequest evaluationRequest = TestHelperUtils.createEvaluationRequest();
        evaluationRequest.setEvaluationMethod(EvaluationMethod.CROSS_VALIDATION);
        evaluationRequest.setNumFolds(TestHelperUtils.NUM_FOLDS);
        evaluationRequest.setNumTests(TestHelperUtils.NUM_TESTS);
        ClassificationResult result = evaluationService.evaluateModel(evaluationRequest);
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getEvaluationResults().getEvaluation().isKCrossValidationMethod()).isTrue();
    }

    @Test
    public void testClassificationResultWithError() throws Exception {
        Evaluation evaluation = PowerMockito.mock(Evaluation.class);
        PowerMockito.whenNew(Evaluation.class).withAnyArguments().thenReturn(evaluation);
        doThrow(new Exception()).when(evaluation).kCrossValidateModel(any(Classifier.class), any(Instances.class),
                anyInt(), anyInt(), any(Random.class));
        EvaluationRequest evaluationRequest = TestHelperUtils.createEvaluationRequest();
        evaluationRequest.setEvaluationMethod(EvaluationMethod.CROSS_VALIDATION);
        ClassificationResult result = evaluationService.evaluateModel(evaluationRequest);
        assertThat(result.isSuccess()).isFalse();
    }
}
