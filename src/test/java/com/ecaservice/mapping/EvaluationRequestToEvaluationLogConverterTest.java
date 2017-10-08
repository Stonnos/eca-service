package com.ecaservice.mapping;

import com.ecaservice.TestDataHelper;
import com.ecaservice.model.EvaluationMethod;
import com.ecaservice.model.EvaluationRequest;
import com.ecaservice.model.InputData;
import com.ecaservice.model.entity.EvaluationLog;
import eca.metrics.KNearestNeighbours;
import org.junit.Test;

import java.util.HashMap;

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
        evaluationRequest.setIpAddress(TestDataHelper.IP_ADDRESS);
        evaluationRequest.setEvaluationMethod(EvaluationMethod.TRAINING_DATA);
        evaluationRequest.setEvaluationOptionsMap(new HashMap<>());

        InputData inputData = new InputData(new KNearestNeighbours(),
                TestDataHelper.generate(TestDataHelper.NUM_INSTANCES, TestDataHelper.NUM_ATTRIBUTES));
        evaluationRequest.setInputData(inputData);

        EvaluationLog evaluationLog = mapper.map(evaluationRequest, EvaluationLog.class);

        assertNotNull(evaluationLog);
        assertEquals(evaluationRequest.getEvaluationMethod(), evaluationLog.getEvaluationMethod());
        assertEquals(evaluationRequest.getIpAddress(), evaluationLog.getIpAddress());
        assertNotNull(evaluationLog.getEvaluationOptionsMap());
        assertEquals(evaluationRequest.getInputData().getData().relationName(),
                evaluationLog.getInstancesInfo().getRelationName());
        assertEquals(evaluationRequest.getInputData().getData().numAttributes(),
                evaluationLog.getInstancesInfo().getNumAttributes().intValue());
        assertEquals(evaluationRequest.getInputData().getData().numClasses(),
                evaluationLog.getInstancesInfo().getNumClasses().intValue());
        assertEquals(evaluationRequest.getInputData().getData().numInstances(),
                evaluationLog.getInstancesInfo().getNumInstances().intValue());
        assertNotNull(evaluationLog.getInputOptionsMap());
        assertFalse(evaluationLog.getInputOptionsMap().isEmpty());
    }
}
