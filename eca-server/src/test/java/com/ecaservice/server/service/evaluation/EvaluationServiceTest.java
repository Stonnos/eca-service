package com.ecaservice.server.service.evaluation;

import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.config.CrossValidationConfig;
import com.ecaservice.server.model.evaluation.ClassificationResult;
import com.ecaservice.server.model.evaluation.EvaluationRequestDataModel;
import eca.core.evaluation.EvaluationMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests that checks EvaluationService functionality {@see EvaluationService}.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties
@TestPropertySource("classpath:application.properties")
@Import(CrossValidationConfig.class)
class EvaluationServiceTest {

    @Inject
    private CrossValidationConfig config;

    private EvaluationService evaluationService;

    @BeforeEach
    void setUp() {
        evaluationService = new EvaluationService(config);
    }

    @Test
    void testTrainingDataMethod() {
        EvaluationRequestDataModel evaluationRequest = TestHelperUtils.createEvaluationRequestData();
        ClassificationResult result = evaluationService.evaluateModel(evaluationRequest);
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getEvaluationResults().getEvaluation().isKCrossValidationMethod()).isFalse();
    }

    @Test
    void testCrossValidationMethod() {
        EvaluationRequestDataModel evaluationRequest = TestHelperUtils.createEvaluationRequestData();
        evaluationRequest.setEvaluationMethod(EvaluationMethod.CROSS_VALIDATION);
        evaluationRequest.setNumFolds(TestHelperUtils.NUM_FOLDS);
        evaluationRequest.setNumTests(TestHelperUtils.NUM_TESTS);
        ClassificationResult result = evaluationService.evaluateModel(evaluationRequest);
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getEvaluationResults().getEvaluation().isKCrossValidationMethod()).isTrue();
    }
}
