package com.ecaservice.mapping;

import com.ecaservice.TestDataHelper;
import com.ecaservice.dictionary.EcaServiceDictionary;
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
    public void testEvaluationRequestToEvaluationLogConversionInCrossValidation() {
        EvaluationRequest evaluationRequest = new EvaluationRequest();
        evaluationRequest.setIpAddress(TestDataHelper.IP_ADDRESS);
        evaluationRequest.setRequestDate(LocalDateTime.now());
        evaluationRequest.setEvaluationMethod(EvaluationMethod.CROSS_VALIDATION);

        InputData inputData = new InputData(new KNearestNeighbours(),
                TestDataHelper.generate(TestDataHelper.NUM_INSTANCES, TestDataHelper.NUM_ATTRIBUTES));
        evaluationRequest.setInputData(inputData);
        evaluationRequest.setNumFolds(TestDataHelper.NUM_FOLDS);
        evaluationRequest.setNumTests(TestDataHelper.NUM_TESTS);

        EvaluationLog evaluationLog = mapper.map(evaluationRequest, EvaluationLog.class);

        assertNotNull(evaluationLog);
        assertEquals(evaluationRequest.getEvaluationMethod(), evaluationLog.getEvaluationMethod());
        assertEquals(evaluationRequest.getIpAddress(), evaluationLog.getIpAddress());
        assertEquals(evaluationRequest.getRequestDate(), evaluationLog.getRequestDate());
        assertEquals(evaluationRequest.getNumFolds(),
                Integer.valueOf(evaluationLog.getEvaluationOptionsMap().get(EcaServiceDictionary.NUMBER_OF_FOLDS)));
        assertEquals(evaluationRequest.getNumTests(),
                Integer.valueOf(evaluationLog.getEvaluationOptionsMap().get(EcaServiceDictionary.NUMBER_OF_FOLDS)));
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
