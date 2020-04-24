package com.ecaservice.service.evaluation;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.config.CrossValidationConfig;
import com.ecaservice.dto.EvaluationRequest;
import com.ecaservice.model.evaluation.ClassificationResult;
import eca.core.evaluation.EvaluationMethod;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests that checks EvaluationService functionality {@see EvaluationService}.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
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
}
