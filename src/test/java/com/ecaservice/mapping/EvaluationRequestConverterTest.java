package com.ecaservice.mapping;

import com.ecaservice.TestDataBuilder;
import com.ecaservice.dto.EvaluationRequestDto;
import com.ecaservice.model.EvaluationRequest;
import com.ecaservice.model.entity.EvaluationMethod;
import eca.metrics.KNearestNeighbours;
import org.junit.Before;
import org.junit.Test;
import weka.core.Instances;

import static org.junit.Assert.*;

/**
 *  Unit tests that checks EvaluationRequestConverter functionality
 * (see {@link EvaluationRequestConverter}).
 * @author Roman Batygin
 */
public class EvaluationRequestConverterTest extends AbstractConverterTest {

    private Instances instances;

    @Before
    public void setUp() {
        instances = TestDataBuilder.generate(25, 6);
    }

    @Test
    public void testEvaluationRequestWithTrainingSetMethod() {
        EvaluationRequestDto evaluationRequestDto = new EvaluationRequestDto();
        evaluationRequestDto.setClassifier(new KNearestNeighbours());
        evaluationRequestDto.setData(instances);
        evaluationRequestDto.setEvaluationMethod(EvaluationMethod.TRAINING_DATA);
        evaluationRequestDto.setNumFolds(10);
        evaluationRequestDto.setNumTests(10);

        EvaluationRequest evaluationRequest = mapper.map(evaluationRequestDto, EvaluationRequest.class);

        assertNotNull(evaluationRequest);
        assertNotNull(evaluationRequest.getInputData());
        assertEquals(evaluationRequestDto.getClassifier(), evaluationRequest.getInputData().getClassifier());
        assertEquals(evaluationRequestDto.getData(), evaluationRequest.getInputData().getData());
        assertEquals(evaluationRequestDto.getEvaluationMethod(), evaluationRequest.getEvaluationMethod());
        assertNull(evaluationRequest.getNumFolds());
        assertNull(evaluationRequest.getNumTests());
    }

    @Test
    public void testEvaluationRequestWithCrossValidationMethod() {
        EvaluationRequestDto evaluationRequestDto = new EvaluationRequestDto();
        evaluationRequestDto.setClassifier(new KNearestNeighbours());
        evaluationRequestDto.setData(instances);
        evaluationRequestDto.setEvaluationMethod(EvaluationMethod.CROSS_VALIDATION);
        evaluationRequestDto.setNumFolds(10);
        evaluationRequestDto.setNumTests(10);

        EvaluationRequest evaluationRequest = mapper.map(evaluationRequestDto, EvaluationRequest.class);

        assertNotNull(evaluationRequest);
        assertNotNull(evaluationRequest.getInputData());
        assertEquals(evaluationRequestDto.getClassifier(), evaluationRequest.getInputData().getClassifier());
        assertEquals(evaluationRequestDto.getData(), evaluationRequest.getInputData().getData());
        assertEquals(evaluationRequestDto.getEvaluationMethod(), evaluationRequest.getEvaluationMethod());
        assertEquals(evaluationRequestDto.getNumFolds(), evaluationRequest.getNumFolds());
        assertEquals(evaluationRequestDto.getNumTests(), evaluationRequest.getNumTests());
    }
}
