package com.ecaservice.mapping;

import com.ecaservice.TestDataHelper;
import com.ecaservice.dto.EvaluationRequestDto;
import com.ecaservice.model.evaluation.EvaluationMethod;
import com.ecaservice.model.evaluation.EvaluationOption;
import com.ecaservice.model.evaluation.EvaluationRequest;
import eca.metrics.KNearestNeighbours;
import org.junit.Before;
import org.junit.Test;
import weka.core.Instances;

import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Unit tests that checks EvaluationRequestConverter functionality
 * (see {@link EvaluationRequestConverter}).
 *
 * @author Roman Batygin
 */
public class EvaluationRequestConverterTest extends AbstractConverterTest {

    private Instances instances;

    @Before
    public void setUp() {
        instances = TestDataHelper.generate(TestDataHelper.NUM_INSTANCES, TestDataHelper.NUM_ATTRIBUTES);
    }

    @Test
    public void testEvaluationRequestWithTrainingSetMethod() {
        EvaluationRequestDto evaluationRequestDto = new EvaluationRequestDto();
        evaluationRequestDto.setClassifier(new KNearestNeighbours());
        evaluationRequestDto.setData(instances);
        evaluationRequestDto.setEvaluationMethod(EvaluationMethod.TRAINING_DATA);
        EvaluationRequest evaluationRequest = mapper.map(evaluationRequestDto, EvaluationRequest.class);
        assertNotNull(evaluationRequest);
        assertNotNull(evaluationRequest.getInputData());
        assertEquals(evaluationRequestDto.getClassifier(), evaluationRequest.getInputData().getClassifier());
        assertEquals(evaluationRequestDto.getData(), evaluationRequest.getInputData().getData());
        assertEquals(evaluationRequestDto.getEvaluationMethod(), evaluationRequest.getEvaluationMethod());
        assertTrue(evaluationRequest.getEvaluationOptionsMap().isEmpty());
    }

    @Test
    public void testEvaluationRequestWithCrossValidationMethod() {
        EvaluationRequestDto evaluationRequestDto = new EvaluationRequestDto();
        evaluationRequestDto.setClassifier(new KNearestNeighbours());
        evaluationRequestDto.setData(instances);
        evaluationRequestDto.setEvaluationMethod(EvaluationMethod.CROSS_VALIDATION);
        evaluationRequestDto.setEvaluationOptionsMap(new HashMap<>());
        evaluationRequestDto.getEvaluationOptionsMap().put(EvaluationOption.NUM_FOLDS,
                String.valueOf(TestDataHelper.NUM_FOLDS));
        evaluationRequestDto.getEvaluationOptionsMap().put(EvaluationOption.NUM_TESTS,
                String.valueOf(TestDataHelper.NUM_TESTS));
        EvaluationRequest evaluationRequest = mapper.map(evaluationRequestDto, EvaluationRequest.class);
        assertNotNull(evaluationRequest);
        assertNotNull(evaluationRequest.getInputData());
        assertEquals(evaluationRequestDto.getClassifier(), evaluationRequest.getInputData().getClassifier());
        assertEquals(evaluationRequestDto.getData(), evaluationRequest.getInputData().getData());
        assertEquals(evaluationRequestDto.getEvaluationMethod(), evaluationRequest.getEvaluationMethod());
        assertEquals(evaluationRequestDto.getEvaluationOptionsMap().get(EvaluationOption.NUM_FOLDS),
                evaluationRequest.getEvaluationOptionsMap().get(EvaluationOption.NUM_FOLDS));
        assertEquals(evaluationRequestDto.getEvaluationOptionsMap().get(EvaluationOption.NUM_TESTS),
                evaluationRequest.getEvaluationOptionsMap().get(EvaluationOption.NUM_TESTS));
    }
}
