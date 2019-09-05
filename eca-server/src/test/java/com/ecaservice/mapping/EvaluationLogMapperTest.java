package com.ecaservice.mapping;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.dto.EvaluationRequest;
import com.ecaservice.model.entity.ClassifierInputOptions;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.web.dto.model.EvaluationLogDetailsDto;
import com.ecaservice.web.dto.model.EvaluationLogDto;
import eca.core.evaluation.EvaluationMethod;
import eca.metrics.KNearestNeighbours;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests that checks EvaluationLogMapper functionality {@see EvaluationLogMapper}.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@Import({EvaluationLogMapperImpl.class, InstancesInfoMapperImpl.class,
        ClassifierInputOptionsMapperImpl.class, ClassifierInfoMapperImpl.class})
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
        assertThat(evaluationLog.getClassifierInfo()).isNotNull();
        assertThat(evaluationLog.getClassifierInfo().getClassifierInputOptions()).isNotNull();
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
        evaluationLog.setInstancesInfo(TestHelperUtils.createInstancesInfo());
        EvaluationLogDto evaluationLogDto = evaluationLogMapper.map(evaluationLog);
        assertEvaluationLogDto(evaluationLogDto, evaluationLog);
    }

    @Test
    public void testMapToEvaluationLogDetailsDto() {
        EvaluationLog evaluationLog = TestHelperUtils.createEvaluationLog();
        evaluationLog.setEvaluationOptionsMap(
                TestHelperUtils.createEvaluationOptionsMap(TestHelperUtils.NUM_FOLDS, TestHelperUtils.NUM_TESTS));
        evaluationLog.setInstancesInfo(TestHelperUtils.createInstancesInfo());
        EvaluationLogDetailsDto evaluationLogDetailsDto = evaluationLogMapper.mapDetails(evaluationLog);
        assertEvaluationLogDto(evaluationLogDetailsDto, evaluationLog);
    }

    private void assertEvaluationLogDto(EvaluationLogDto evaluationLogDto, EvaluationLog evaluationLog) {
        assertThat(evaluationLogDto).isNotNull();
        assertThat(evaluationLogDto.getClassifierInfo()).isNotNull();
        assertThat(evaluationLogDto.getClassifierInfo().getClassifierName()).isEqualTo(
                evaluationLog.getClassifierInfo().getClassifierName());
        assertThat(evaluationLogDto.getCreationDate()).isEqualTo(evaluationLog.getCreationDate());
        assertThat(evaluationLogDto.getStartDate()).isEqualTo(evaluationLog.getStartDate());
        assertThat(evaluationLogDto.getEndDate()).isEqualTo(evaluationLog.getEndDate());
        assertThat(evaluationLogDto.getEvaluationMethod().getDescription()).isEqualTo(
                evaluationLog.getEvaluationMethod().getDescription());
        assertThat(evaluationLogDto.getEvaluationMethod().getValue()).isEqualTo(
                evaluationLog.getEvaluationMethod().name());
        assertThat(evaluationLogDto.getEvaluationStatus().getDescription()).isEqualTo(
                evaluationLog.getEvaluationStatus().getDescription());
        assertThat(evaluationLogDto.getEvaluationStatus().getValue()).isEqualTo(
                evaluationLog.getEvaluationStatus().name());
        assertThat(evaluationLogDto.getRequestId()).isEqualTo(evaluationLog.getRequestId());
        assertThat(evaluationLogDto.getClassifierInfo().getInputOptions()).isNotNull();
        assertThat(evaluationLogDto.getClassifierInfo().getInputOptions()).hasSameSizeAs(
                evaluationLog.getClassifierInfo().getClassifierInputOptions());
        assertThat(evaluationLogDto.getNumFolds()).isNotNull();
        assertThat(evaluationLogDto.getNumTests()).isNotNull();
        assertThat(evaluationLogDto.getSeed()).isNull();
        assertThat(evaluationLogDto.getInstancesInfo()).isNotNull();
    }

    private void assertOptions(EvaluationLog evaluationLog, EvaluationRequest request) {
        List<ClassifierInputOptions> classifierInputOptions =
                evaluationLog.getClassifierInfo().getClassifierInputOptions();
        assertThat(classifierInputOptions).isNotNull();
        assertThat(classifierInputOptions).isNotEmpty();
        String[] options = request.getClassifier().getOptions();
        assertThat(classifierInputOptions.size()).isEqualTo(options.length / 2);
        for (int i = 0; i < options.length; i += 2) {
            ClassifierInputOptions inputOptions = classifierInputOptions.get(i / 2);
            assertThat(inputOptions.getOptionName()).isEqualTo(options[i]);
            assertThat(inputOptions.getOptionValue()).isEqualTo(options[i + 1]);
            assertThat(inputOptions.getOptionOrder()).isEqualTo(i / 2);
        }
    }
}
