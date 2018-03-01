package com.ecaservice.mapping;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.model.InputData;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.evaluation.EvaluationMethod;
import com.ecaservice.model.evaluation.EvaluationRequest;
import eca.metrics.KNearestNeighbours;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests that checks EvaluationLogMapper functionality {@see EvaluationLogMapper}.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@Import(EvaluationLogMapperImpl.class)
public class EvaluationLogMapperTest {

    @Inject
    private EvaluationLogMapper evaluationLogMapper;

    @Test
    public void testMapToEvaluationLog() throws Exception {
        EvaluationRequest evaluationRequest = new EvaluationRequest();
        evaluationRequest.setIpAddress(TestHelperUtils.IP_ADDRESS);
        evaluationRequest.setEvaluationMethod(EvaluationMethod.TRAINING_DATA);
        evaluationRequest.setEvaluationOptionsMap(new HashMap<>());
        InputData inputData = new InputData(new KNearestNeighbours(),
                TestHelperUtils.loadInstances());
        evaluationRequest.setInputData(inputData);
        EvaluationLog evaluationLog = evaluationLogMapper.map(evaluationRequest);

        assertThat(evaluationLog).isNotNull();
        assertThat(evaluationLog.getEvaluationMethod()).isEqualTo(evaluationRequest.getEvaluationMethod());
        assertThat(evaluationLog.getIpAddress()).isEqualTo(evaluationRequest.getIpAddress());
        assertThat(evaluationLog.getEvaluationOptionsMap()).isNotNull();
        assertThat(evaluationLog.getInstancesInfo().getRelationName()).isEqualTo(
                evaluationRequest.getInputData().getData().relationName());
        assertThat(evaluationLog.getInstancesInfo().getClassName()).isEqualTo(
                evaluationRequest.getInputData().getData().classAttribute().name());
        assertThat(evaluationLog.getInstancesInfo().getNumAttributes().intValue()).isEqualTo(
                evaluationRequest.getInputData().getData().numAttributes());
        assertThat(evaluationLog.getInstancesInfo().getNumClasses().intValue()).isEqualTo(
                evaluationRequest.getInputData().getData().numClasses());
        assertThat(evaluationLog.getInstancesInfo().getNumInstances().intValue()).isEqualTo(
                evaluationRequest.getInputData().getData().numInstances());
        assertOptions(evaluationLog, evaluationRequest);
    }

    private void assertOptions(EvaluationLog evaluationLog, EvaluationRequest request) {
        Map<String, String> inputOptionsMap = evaluationLog.getInputOptionsMap();
        assertThat(inputOptionsMap).isNotNull();
        assertThat(inputOptionsMap).isNotEmpty();
        String[] options = request.getInputData().getClassifier().getOptions();
        assertThat(inputOptionsMap.size()).isEqualTo(options.length / 2);
        for (int i = 0; i < options.length; i += 2) {
            assertThat(options[i + 1]).isEqualTo(inputOptionsMap.get(options[i]));
        }
    }
}
