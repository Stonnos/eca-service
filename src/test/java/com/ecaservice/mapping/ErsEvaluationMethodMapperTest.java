package com.ecaservice.mapping;

import com.ecaservice.dto.evaluation.EvaluationMethod;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

/**
 * Unit tests for checking {@link ErsEvaluationMethodMapper} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ErsEvaluationMethodMapperTest {

    @Inject
    private ErsEvaluationMethodMapper ersEvaluationMethodMapper;

    @Test
    public void testMapTrainingDataMethod() {
        Assertions.assertThat(ersEvaluationMethodMapper.map(EvaluationMethod.TRAINING_DATA)).isEqualTo(com.ecaservice.model.evaluation.EvaluationMethod.TRAINING_DATA);
    }

    @Test
    public void testMapCrossValidationMethod() {
        Assertions.assertThat(ersEvaluationMethodMapper.map(EvaluationMethod.CROSS_VALIDATION)).isEqualTo(com.ecaservice.model.evaluation.EvaluationMethod.CROSS_VALIDATION);
    }
}
