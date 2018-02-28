package com.ecaservice.mapping;

import com.ecaservice.model.evaluation.EvaluationMethod;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests that checks EvaluationMethodMapper functionality {@see EvaluationMethodMapper}.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@Import(EvaluationMethodMapperImpl.class)
public class EvaluationMethodMapperTest {

    @Autowired
    private EvaluationMethodMapper evaluationMethodMapper;

    @Test
    public void testMapTrainingDataMethod() {
        eca.core.evaluation.EvaluationMethod evaluationMethod =
                evaluationMethodMapper.map(EvaluationMethod.TRAINING_DATA);
        assertThat(evaluationMethod).isEqualTo(eca.core.evaluation.EvaluationMethod.TRAINING_DATA);
    }

    @Test
    public void testMapCrossValidationMethod() {
        eca.core.evaluation.EvaluationMethod evaluationMethod =
                evaluationMethodMapper.map(EvaluationMethod.CROSS_VALIDATION);
        assertThat(evaluationMethod).isEqualTo(eca.core.evaluation.EvaluationMethod.CROSS_VALIDATION);
    }
}
