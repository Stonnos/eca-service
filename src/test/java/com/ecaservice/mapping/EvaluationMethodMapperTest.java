package com.ecaservice.mapping;

import com.ecaservice.model.evaluation.EvaluationMethod;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests that checks EvaluationMethodMapper functionality
 * (see {@link EvaluationMethodMapper}).
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
        assertEquals(evaluationMethod, eca.core.evaluation.EvaluationMethod.TRAINING_DATA);
    }

    @Test
    public void testMapCrossValidationMethod() {
        eca.core.evaluation.EvaluationMethod evaluationMethod =
                evaluationMethodMapper.map(EvaluationMethod.CROSS_VALIDATION);
        assertEquals(evaluationMethod, eca.core.evaluation.EvaluationMethod.CROSS_VALIDATION);
    }
}
