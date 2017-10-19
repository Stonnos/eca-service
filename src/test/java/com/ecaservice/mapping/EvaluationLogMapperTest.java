package com.ecaservice.mapping;

import com.ecaservice.TestDataHelper;
import com.ecaservice.model.InputData;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.evaluation.EvaluationMethod;
import com.ecaservice.model.evaluation.EvaluationRequest;
import eca.metrics.KNearestNeighbours;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests that checks EvaluationLogMapper functionality
 * (see {@link EvaluationLogMapper}).
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@Import(EvaluationLogMapperImpl.class)
public class EvaluationLogMapperTest {

    @Autowired
    private EvaluationLogMapper evaluationLogMapper;

    @Test
    public void testMapToEvaluationLog() {
        EvaluationRequest evaluationRequest = new EvaluationRequest();
        evaluationRequest.setIpAddress(TestDataHelper.IP_ADDRESS);
        evaluationRequest.setEvaluationMethod(EvaluationMethod.TRAINING_DATA);
        evaluationRequest.setEvaluationOptionsMap(new HashMap<>());
        InputData inputData = new InputData(new KNearestNeighbours(),
                TestDataHelper.generateInstances(TestDataHelper.NUM_INSTANCES, TestDataHelper.NUM_ATTRIBUTES));
        evaluationRequest.setInputData(inputData);
        EvaluationLog evaluationLog = evaluationLogMapper.map(evaluationRequest);

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

        assertOptions(evaluationLog, evaluationRequest);
    }

    private void assertOptions(EvaluationLog evaluationLog, EvaluationRequest request) {
        Map<String, String> inputOptionsMap = evaluationLog.getInputOptionsMap();
        assertNotNull(inputOptionsMap);
        assertThat(inputOptionsMap).isNotEmpty();
        String[] options = request.getInputData().getClassifier().getOptions();
        assertEquals(inputOptionsMap.size(), options.length / 2);

        for (int i = 0; i < options.length; i += 2) {
            assertEquals(options[i + 1], inputOptionsMap.get(options[i]));
        }
    }
}
