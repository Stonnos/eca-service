package com.ecaservice.server.mapping;

import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.config.CrossValidationConfig;
import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.server.model.evaluation.EvaluationRequestDataModel;
import com.ecaservice.server.report.model.EvaluationLogBean;
import com.ecaservice.web.dto.model.EvaluationLogDetailsDto;
import com.ecaservice.web.dto.model.EvaluationLogDto;
import eca.core.evaluation.EvaluationMethod;
import eca.metrics.KNearestNeighbours;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

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
        EvaluationRequestDataModel evaluationRequestDataModel = new EvaluationRequestDataModel();
        evaluationRequestDataModel.setEvaluationMethod(EvaluationMethod.TRAINING_DATA);
        evaluationRequestDataModel.setData(TestHelperUtils.loadInstances());
        evaluationRequestDataModel.setClassifier(new KNearestNeighbours());
        EvaluationLog evaluationLog = evaluationLogMapper.map(evaluationRequestDataModel, crossValidationConfig);

        assertThat(evaluationLog).isNotNull();
        assertThat(evaluationLog.getEvaluationMethod()).isEqualTo(evaluationRequestDataModel.getEvaluationMethod());
        assertThat(evaluationLog.getClassifierInfo()).isNotNull();
        assertThat(evaluationLog.getNumFolds()).isNull();
        assertThat(evaluationLog.getNumTests()).isNull();
        assertThat(evaluationLog.getSeed()).isNull();
    }

    @Test
    void testMapToEvaluationLogWithCrossValidationEvaluationMethod() {
        EvaluationRequestDataModel evaluationRequestDataModel = new EvaluationRequestDataModel();
        evaluationRequestDataModel.setEvaluationMethod(EvaluationMethod.CROSS_VALIDATION);
        evaluationRequestDataModel.setNumFolds(TestHelperUtils.NUM_FOLDS);
        evaluationRequestDataModel.setNumFolds(TestHelperUtils.NUM_TESTS);
        evaluationRequestDataModel.setNumFolds(TestHelperUtils.SEED);
        EvaluationLog evaluationLog = evaluationLogMapper.map(evaluationRequestDataModel, crossValidationConfig);
        assertThat(evaluationLog).isNotNull();
        assertThat(evaluationLog.getNumFolds()).isEqualTo(evaluationRequestDataModel.getNumFolds());
        assertThat(evaluationLog.getNumTests()).isEqualTo(evaluationRequestDataModel.getNumTests());
        assertThat(evaluationLog.getSeed()).isEqualTo(evaluationRequestDataModel.getSeed());
    }

    @Test
    void testMapToEvaluationLogWithDefaultOptions() {
        EvaluationRequestDataModel evaluationRequestDataModel = new EvaluationRequestDataModel();
        evaluationRequestDataModel.setEvaluationMethod(EvaluationMethod.CROSS_VALIDATION);
        EvaluationLog evaluationLog = evaluationLogMapper.map(evaluationRequestDataModel, crossValidationConfig);
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
        assertThat(evaluationLogBean.getModelPath()).isEqualTo(evaluationLog.getModelPath());
        assertThat(evaluationLogBean.getPctCorrect()).isEqualTo(evaluationLog.getPctCorrect());
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
        assertThat(evaluationLogDto.getModelPath()).isEqualTo(evaluationLog.getModelPath());
        assertThat(evaluationLogDto.getPctCorrect()).isEqualTo(evaluationLog.getPctCorrect());
    }
}
