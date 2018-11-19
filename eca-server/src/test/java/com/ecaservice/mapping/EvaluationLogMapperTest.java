package com.ecaservice.mapping;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.dto.EvaluationRequest;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.web.dto.EvaluationLogDto;
import eca.core.evaluation.EvaluationMethod;
import eca.metrics.KNearestNeighbours;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests that checks EvaluationLogMapper functionality {@see EvaluationLogMapper}.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@Import({EvaluationLogMapperImpl.class, InstancesInfoMapperImpl.class})
public class EvaluationLogMapperTest {

    @Inject
    private EvaluationLogMapper evaluationLogMapper;

    @Test
    public void testMapToEvaluationLog() throws Exception {
        EvaluationRequest evaluationRequest = new EvaluationRequest();
        evaluationRequest.setEvaluationMethod(EvaluationMethod.TRAINING_DATA);
        evaluationRequest.setEvaluationOptionsMap(new HashMap<>());
        evaluationRequest.setData(TestHelperUtils.loadInstances());
        evaluationRequest.setClassifier(new KNearestNeighbours());
        EvaluationLog evaluationLog = evaluationLogMapper.map(evaluationRequest);

        assertThat(evaluationLog).isNotNull();
        assertThat(evaluationLog.getEvaluationMethod()).isEqualTo(evaluationRequest.getEvaluationMethod());
        assertThat(evaluationLog.getEvaluationOptionsMap()).isNotNull();
        assertThat(evaluationLog.getInstancesInfo().getRelationName()).isEqualTo(
                evaluationRequest.getData().relationName());
        assertThat(evaluationLog.getInstancesInfo().getClassName()).isEqualTo(
                evaluationRequest.getData().classAttribute().name());
        assertThat(evaluationLog.getInstancesInfo().getNumAttributes().intValue()).isEqualTo(
                evaluationRequest.getData().numAttributes());
        assertThat(evaluationLog.getInstancesInfo().getNumClasses().intValue()).isEqualTo(
                evaluationRequest.getData().numClasses());
        assertThat(evaluationLog.getInstancesInfo().getNumInstances().intValue()).isEqualTo(
                evaluationRequest.getData().numInstances());
        assertOptions(evaluationLog, evaluationRequest);
    }

    @Test
    public void testMapToEvaluationLogDto() {
        EvaluationLog evaluationLog = TestHelperUtils.createEvaluationLog();
        evaluationLog.setEvaluationOptionsMap(
                TestHelperUtils.createEvaluationOptionsMap(TestHelperUtils.NUM_FOLDS, TestHelperUtils.NUM_TESTS));
        evaluationLog.setInputOptionsMap(Collections.singletonMap("Key", "Value"));
        evaluationLog.setInstancesInfo(TestHelperUtils.createInstancesInfo());
        EvaluationLogDto evaluationLogDto = evaluationLogMapper.map(evaluationLog);
        assertThat(evaluationLogDto).isNotNull();
        assertThat(evaluationLogDto.getClassifierName()).isEqualTo(evaluationLog.getClassifierName());
        assertThat(evaluationLogDto.getCreationDate()).isEqualTo(evaluationLog.getCreationDate());
        assertThat(evaluationLogDto.getStartDate()).isEqualTo(evaluationLog.getStartDate());
        assertThat(evaluationLogDto.getEndDate()).isEqualTo(evaluationLog.getEndDate());
        assertThat(evaluationLogDto.getEvaluationMethod()).isEqualTo(
                evaluationLog.getEvaluationMethod().getDescription());
        assertThat(evaluationLogDto.getEvaluationStatus()).isEqualTo(
                evaluationLog.getEvaluationStatus().getDescription());
        assertThat(evaluationLogDto.getRequestId()).isEqualTo(evaluationLog.getRequestId());
        assertThat(evaluationLogDto.getInputOptions()).isNotNull();
        assertThat(evaluationLogDto.getInputOptions().size()).isOne();
        assertThat(evaluationLogDto.getNumFolds()).isNotNull();
        assertThat(evaluationLogDto.getNumTests()).isNotNull();
        assertThat(evaluationLogDto.getSeed()).isNull();
        assertThat(evaluationLogDto.getInstancesInfo()).isNotNull();
    }

    private void assertOptions(EvaluationLog evaluationLog, EvaluationRequest request) {
        Map<String, String> inputOptionsMap = evaluationLog.getInputOptionsMap();
        assertThat(inputOptionsMap).isNotNull();
        assertThat(inputOptionsMap).isNotEmpty();
        String[] options = request.getClassifier().getOptions();
        assertThat(inputOptionsMap.size()).isEqualTo(options.length / 2);
        for (int i = 0; i < options.length; i += 2) {
            assertThat(options[i + 1]).isEqualTo(inputOptionsMap.get(options[i]));
        }
    }
}
