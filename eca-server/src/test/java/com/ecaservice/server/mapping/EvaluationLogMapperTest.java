package com.ecaservice.server.mapping;

import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.base.model.EvaluationRequest;
import com.ecaservice.server.report.model.EvaluationLogBean;
import com.ecaservice.server.config.CrossValidationConfig;
import com.ecaservice.server.model.entity.ClassifierInputOptions;
import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.web.dto.model.EvaluationLogDetailsDto;
import com.ecaservice.web.dto.model.EvaluationLogDto;
import eca.core.evaluation.EvaluationMethod;
import eca.metrics.KNearestNeighbours;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests that checks EvaluationLogMapper functionality {@see EvaluationLogMapper}.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import({EvaluationLogMapperImpl.class, InstancesInfoMapperImpl.class, DateTimeConverter.class,
        ClassifierInfoMapperImpl.class, CrossValidationConfig.class})
class EvaluationLogMapperTest {

    @Inject
    private CrossValidationConfig crossValidationConfig;
    @Inject
    private EvaluationLogMapper evaluationLogMapper;

    @Test
    void testMapToEvaluationLogWithTrainingDataEvaluationMethod() {
        EvaluationRequest evaluationRequest = new EvaluationRequest();
        evaluationRequest.setEvaluationMethod(EvaluationMethod.TRAINING_DATA);
        evaluationRequest.setData(TestHelperUtils.loadInstances());
        evaluationRequest.setClassifier(new KNearestNeighbours());
        EvaluationLog evaluationLog = evaluationLogMapper.map(evaluationRequest, crossValidationConfig);

        assertThat(evaluationLog).isNotNull();
        assertThat(evaluationLog.getEvaluationMethod()).isEqualTo(evaluationRequest.getEvaluationMethod());
        assertThat(evaluationLog.getClassifierInfo()).isNotNull();
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
        assertThat(evaluationLog.getInstancesInfo().getDataMd5Hash()).isNotNull();
        assertThat(evaluationLog.getNumFolds()).isNull();
        assertThat(evaluationLog.getNumTests()).isNull();
        assertThat(evaluationLog.getSeed()).isNull();
        assertOptions(evaluationLog, evaluationRequest);
    }

    @Test
    void testMapToEvaluationLogWithCrossValidationEvaluationMethod() {
        EvaluationRequest evaluationRequest = new EvaluationRequest();
        evaluationRequest.setEvaluationMethod(EvaluationMethod.CROSS_VALIDATION);
        evaluationRequest.setNumFolds(TestHelperUtils.NUM_FOLDS);
        evaluationRequest.setNumFolds(TestHelperUtils.NUM_TESTS);
        evaluationRequest.setNumFolds(TestHelperUtils.SEED);
        EvaluationLog evaluationLog = evaluationLogMapper.map(evaluationRequest, crossValidationConfig);
        assertThat(evaluationLog).isNotNull();
        assertThat(evaluationLog.getNumFolds()).isEqualTo(evaluationRequest.getNumFolds());
        assertThat(evaluationLog.getNumTests()).isEqualTo(evaluationRequest.getNumTests());
        assertThat(evaluationLog.getSeed()).isEqualTo(evaluationRequest.getSeed());
    }

    @Test
    void testMapToEvaluationLogWithDefaultOptions() {
        EvaluationRequest evaluationRequest = new EvaluationRequest();
        evaluationRequest.setEvaluationMethod(EvaluationMethod.CROSS_VALIDATION);
        EvaluationLog evaluationLog = evaluationLogMapper.map(evaluationRequest, crossValidationConfig);
        assertThat(evaluationLog).isNotNull();
        assertThat(evaluationLog.getNumFolds()).isEqualTo(crossValidationConfig.getNumFolds());
        assertThat(evaluationLog.getNumTests()).isEqualTo(crossValidationConfig.getNumTests());
        assertThat(evaluationLog.getSeed()).isEqualTo(crossValidationConfig.getSeed());
    }

    @Test
    void testMapToEvaluationLogDto() {
        EvaluationLog evaluationLog = TestHelperUtils.createEvaluationLog();
        EvaluationLogDto evaluationLogDto = evaluationLogMapper.map(evaluationLog);
        assertEvaluationLogDto(evaluationLogDto, evaluationLog);
    }

    @Test
    void testMapToEvaluationLogDetailsDto() {
        EvaluationLog evaluationLog = TestHelperUtils.createEvaluationLog();
        EvaluationLogDetailsDto evaluationLogDetailsDto = evaluationLogMapper.mapDetails(evaluationLog);
        assertEvaluationLogDto(evaluationLogDetailsDto, evaluationLog);
    }

    @Test
    void testMapToEvaluationBean() {
        EvaluationLog evaluationLog = TestHelperUtils.createEvaluationLog();
        EvaluationLogBean evaluationLogBean = evaluationLogMapper.mapToBean(evaluationLog);
        assertThat(evaluationLogBean).isNotNull();
        assertThat(evaluationLogBean.getEvaluationMethod()).isNotNull();
        assertThat(evaluationLogBean.getRelationName())
                .isEqualTo(evaluationLog.getInstancesInfo().getRelationName());
        assertThat(evaluationLogBean.getRequestId()).isEqualTo(evaluationLog.getRequestId());
        assertThat(evaluationLogBean.getRequestStatus())
                .isEqualTo(evaluationLog.getRequestStatus().getDescription());
        assertThat(evaluationLogBean.getCreationDate()).isNotNull();
        assertThat(evaluationLogBean.getStartDate()).isNotNull();
        assertThat(evaluationLogBean.getEndDate()).isNotNull();
        assertThat(evaluationLogBean.getEvaluationTotalTime()).isNotNull();
    }

    private void assertEvaluationLogDto(EvaluationLogDto evaluationLogDto, EvaluationLog evaluationLog) {
        assertThat(evaluationLogDto).isNotNull();
        assertThat(evaluationLogDto.getClassifierInfo()).isNull();
        assertThat(evaluationLogDto.getCreationDate()).isEqualTo(evaluationLog.getCreationDate());
        assertThat(evaluationLogDto.getStartDate()).isEqualTo(evaluationLog.getStartDate());
        assertThat(evaluationLogDto.getEndDate()).isEqualTo(evaluationLog.getEndDate());
        assertThat(evaluationLogDto.getEvaluationMethod().getDescription()).isEqualTo(
                evaluationLog.getEvaluationMethod().getDescription());
        assertThat(evaluationLogDto.getEvaluationMethod().getValue()).isEqualTo(
                evaluationLog.getEvaluationMethod().name());
        assertThat(evaluationLogDto.getRequestStatus().getDescription()).isEqualTo(
                evaluationLog.getRequestStatus().getDescription());
        assertThat(evaluationLogDto.getRequestStatus().getValue()).isEqualTo(
                evaluationLog.getRequestStatus().name());
        assertThat(evaluationLogDto.getRequestId()).isEqualTo(evaluationLog.getRequestId());
        assertThat(evaluationLogDto.getNumFolds()).isEqualTo(evaluationLog.getNumFolds());
        assertThat(evaluationLogDto.getNumTests()).isEqualTo(evaluationLog.getNumTests());
        assertThat(evaluationLogDto.getSeed()).isEqualTo(evaluationLog.getSeed());
        assertThat(evaluationLogDto.getInstancesInfo()).isNotNull();
        assertThat(evaluationLogDto.getEvaluationTotalTime()).isNotNull();
    }

    private void assertOptions(EvaluationLog evaluationLog, EvaluationRequest request) {
        List<ClassifierInputOptions> classifierInputOptions =
                evaluationLog.getClassifierInfo().getClassifierInputOptions();
        assertThat(classifierInputOptions).isNotEmpty();
        String[] options = request.getClassifier().getOptions();
        assertThat(classifierInputOptions).hasSize(options.length / 2);
        for (int i = 0; i < options.length; i += 2) {
            ClassifierInputOptions inputOptions = classifierInputOptions.get(i / 2);
            assertThat(inputOptions.getOptionName()).isEqualTo(options[i]);
            assertThat(inputOptions.getOptionValue()).isEqualTo(options[i + 1]);
            assertThat(inputOptions.getOptionOrder()).isEqualTo(i / 2);
        }
    }
}
