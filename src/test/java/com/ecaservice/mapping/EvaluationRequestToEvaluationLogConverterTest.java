package com.ecaservice.mapping;

import com.ecaservice.TestDataBuilder;
import com.ecaservice.model.EvaluationRequest;
import com.ecaservice.model.InputData;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.entity.EvaluationMethod;
import eca.metrics.KNearestNeighbours;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

/**
 * Unit tests that checks EvaluationRequestToEvaluationLogConverter functionality
 * (see {@link EvaluationRequestToEvaluationLogConverter}).
 *
 * @author Roman Batygin
 */
public class EvaluationRequestToEvaluationLogConverterTest extends AbstractConverterTest {

    @Test
    public void testEvaluationRequestToEvaluationLogConversion() {
        EvaluationRequest evaluationRequest = new EvaluationRequest();
        evaluationRequest.setIpAddress("127.0.0.1");
        evaluationRequest.setRequestDate(LocalDateTime.now());
        evaluationRequest.setEvaluationMethod(EvaluationMethod.TRAINING_DATA);

        InputData inputData = new InputData(new KNearestNeighbours(),
                TestDataBuilder.generate(25, 6));
        evaluationRequest.setInputData(inputData);
        evaluationRequest.setNumFolds(10);
        evaluationRequest.setNumTests(10);

        EvaluationLog evaluationLog = mapper.map(evaluationRequest, EvaluationLog.class);

        assertNotNull(evaluationLog);
        assertEquals(evaluationRequest.getEvaluationMethod(), evaluationLog.getEvaluationMethod());
        assertEquals(evaluationRequest.getIpAddress(), evaluationLog.getIpAddress());
        assertEquals(evaluationRequest.getRequestDate(), evaluationLog.getRequestDate());
        assertEquals(evaluationRequest.getNumFolds(), evaluationLog.getEvaluationOptions().getNumFolds());
        assertEquals(evaluationRequest.getNumTests(), evaluationLog.getEvaluationOptions().getNumTests());
        assertEquals(evaluationRequest.getInputData().getData().relationName(),
                evaluationLog.getInstancesInfo().getRelationName());
        assertEquals(evaluationRequest.getInputData().getData().numAttributes(),
                evaluationLog.getInstancesInfo().getNumAttributes().intValue());
        assertEquals(evaluationRequest.getInputData().getData().numClasses(),
                evaluationLog.getInstancesInfo().getNumClasses().intValue());
        assertEquals(evaluationRequest.getInputData().getData().numInstances(),
                evaluationLog.getInstancesInfo().getNumInstances().intValue());
        assertNotNull(evaluationLog.getInputOptionsList());
        assertFalse(evaluationLog.getInputOptionsList().isEmpty());
    }
}
