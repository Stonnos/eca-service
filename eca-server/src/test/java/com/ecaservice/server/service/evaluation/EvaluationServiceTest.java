package com.ecaservice.server.service.evaluation;

import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.config.CrossValidationConfig;
import eca.core.evaluation.EvaluationMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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

    @Autowired
    private CrossValidationConfig config;

    private EvaluationService evaluationService;

    @BeforeEach
    void setUp() {
        evaluationService = new EvaluationService(config);
    }

    @Test
    void testTrainingDataMethod() {
        var evaluationInputDataModel = TestHelperUtils.createEvaluationInputDataModel();
        var result = evaluationService.evaluateModel(evaluationInputDataModel);
        assertThat(result.getEvaluation().isKCrossValidationMethod()).isFalse();
    }

    @Test
    void testCrossValidationMethod() {
        var evaluationInputDataModel = TestHelperUtils.createEvaluationInputDataModel();
        evaluationInputDataModel.setEvaluationMethod(EvaluationMethod.CROSS_VALIDATION);
        evaluationInputDataModel.setNumFolds(TestHelperUtils.NUM_FOLDS);
        evaluationInputDataModel.setNumTests(TestHelperUtils.NUM_TESTS);
        var result = evaluationService.evaluateModel(evaluationInputDataModel);
        assertThat(result.getEvaluation().isKCrossValidationMethod()).isTrue();
    }
}
