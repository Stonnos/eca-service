package com.ecaservice.mapping;

import com.ecaservice.dto.evaluation.EvaluationMethod;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

/**
 * Unit tests for checking {@link ErsEvaluationMethodMapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(ErsEvaluationMethodMapperImpl.class)
public class ErsEvaluationMethodMapperTest {

    @Inject
    private ErsEvaluationMethodMapper ersEvaluationMethodMapper;

    @Test
    public void testMapTrainingDataMethod() {
        Assertions.assertThat(ersEvaluationMethodMapper.map(EvaluationMethod.TRAINING_DATA)).isEqualTo(
                eca.core.evaluation.EvaluationMethod.TRAINING_DATA);
    }

    @Test
    public void testMapCrossValidationMethod() {
        Assertions.assertThat(ersEvaluationMethodMapper.map(EvaluationMethod.CROSS_VALIDATION)).isEqualTo(
                eca.core.evaluation.EvaluationMethod.CROSS_VALIDATION);
    }
}
