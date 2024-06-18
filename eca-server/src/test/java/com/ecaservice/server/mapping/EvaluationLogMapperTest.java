package com.ecaservice.server.mapping;

import com.ecaservice.base.model.InstancesRequest;
import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.config.CrossValidationConfig;
import com.ecaservice.server.model.entity.Channel;
import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.server.model.evaluation.EvaluationRequestData;
import com.ecaservice.server.report.model.EvaluationLogBean;
import com.ecaservice.web.dto.model.EvaluationLogDetailsDto;
import com.ecaservice.web.dto.model.EvaluationLogDto;
import eca.core.evaluation.EvaluationMethod;
import eca.metrics.KNearestNeighbours;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit tests that checks EvaluationLogMapper functionality {@see EvaluationLogMapper}.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:application.properties")
@Import({EvaluationLogMapperImpl.class, InstancesInfoMapperImpl.class, DateTimeConverter.class,
        ClassifierInfoMapperImpl.class, CrossValidationConfig.class})
class EvaluationLogMapperTest {

    @Autowired
    private CrossValidationConfig crossValidationConfig;
    @Autowired
    private EvaluationLogMapper evaluationLogMapper;

    @Test
    void testMapEvaluationRequest() {
        var evaluationRequest = TestHelperUtils.createEvaluationRequest();
        var evaluationRequestDataModel = evaluationLogMapper.map(evaluationRequest);
        assertThat(evaluationRequestDataModel).isNotNull();
        assertThat(evaluationRequestDataModel.getClassifierOptions()).isNotNull();
        assertThat(evaluationRequestDataModel.getEvaluationMethod()).isEqualTo(evaluationRequest.getEvaluationMethod());
        assertThat(evaluationRequestDataModel.getDataUuid()).isEqualTo(evaluationRequest.getDataUuid());
    }

    @Test
    void testMapInstancesRequest() {
        var instancesRequest = new InstancesRequest(UUID.randomUUID().toString());
        Message message = Mockito.mock(Message.class);
        MessageProperties messageProperties = TestHelperUtils.buildMessageProperties();
        when(message.getMessageProperties()).thenReturn(messageProperties);
        var evaluationRequestDataModel = evaluationLogMapper.map(instancesRequest, message, crossValidationConfig);
        assertThat(evaluationRequestDataModel).isNotNull();
        assertThat(evaluationRequestDataModel.getClassifierOptions()).isNull();
        assertThat(evaluationRequestDataModel.getEvaluationMethod()).isEqualTo(EvaluationMethod.CROSS_VALIDATION);
        assertThat(evaluationRequestDataModel.getDataUuid()).isEqualTo(instancesRequest.getDataUuid());
        assertThat(evaluationRequestDataModel.getNumFolds()).isEqualTo(crossValidationConfig.getNumFolds());
        assertThat(evaluationRequestDataModel.getNumTests()).isEqualTo(crossValidationConfig.getNumTests());
        assertThat(evaluationRequestDataModel.getSeed()).isEqualTo(crossValidationConfig.getSeed());
        assertThat(evaluationRequestDataModel.getChannel()).isEqualTo(Channel.QUEUE.name());
        assertThat(evaluationRequestDataModel.isUseOptimalClassifierOptions()).isTrue();
        assertThat(evaluationRequestDataModel.getReplyTo()).isEqualTo(messageProperties.getReplyTo());
        assertThat(evaluationRequestDataModel.getCorrelationId()).isEqualTo(messageProperties.getCorrelationId());
    }

    @Test
    void testMapToEvaluationLogWithTrainingDataEvaluationMethod() {
        EvaluationRequestData evaluationRequestDataModel = new EvaluationRequestData();
        evaluationRequestDataModel.setEvaluationMethod(EvaluationMethod.TRAINING_DATA);
        evaluationRequestDataModel.setDataUuid(UUID.randomUUID().toString());
        evaluationRequestDataModel.setClassifier(new KNearestNeighbours());
        EvaluationLog evaluationLog = evaluationLogMapper.map(evaluationRequestDataModel, crossValidationConfig);
        assertThat(evaluationLog).isNotNull();
        assertThat(evaluationLog.getEvaluationMethod()).isEqualTo(evaluationRequestDataModel.getEvaluationMethod());
        assertThat(evaluationLog.getClassifierInfo()).isNotNull();
        assertThat(evaluationLog.getNumFolds()).isNull();
        assertThat(evaluationLog.getNumTests()).isNull();
        assertThat(evaluationLog.getSeed()).isNull();
        assertThat(evaluationLog.getTrainingDataUuid()).isEqualTo(evaluationRequestDataModel.getDataUuid());
    }

    @Test
    void testMapToEvaluationLogWithCrossValidationEvaluationMethod() {
        EvaluationRequestData evaluationRequestDataModel = new EvaluationRequestData();
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
        EvaluationRequestData evaluationRequestDataModel = new EvaluationRequestData();
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

    @Test
    void testMapToEvaluationLogModel() {
        EvaluationLog evaluationLog = TestHelperUtils.createEvaluationLog();
        var evaluationLogModel = evaluationLogMapper.mapToModel(evaluationLog);
        assertThat(evaluationLogModel).isNotNull();
        assertThat(evaluationLogModel.getRequestId()).isEqualTo(evaluationLog.getRequestId());
        assertThat(evaluationLogModel.getRequestStatus()).isEqualTo(evaluationLog.getRequestStatus().name());
        assertThat(evaluationLogModel.getChannel()).isEqualTo(evaluationLog.getChannel().name());
        assertThat(evaluationLogModel.getClassifierName()).isEqualTo(
                evaluationLog.getClassifierInfo().getClassifierName());
    }

    @Test
    void testMapCreateEvaluationRequestDtoWithTrainingDataMethod() {
        var evaluationRequestDto = TestHelperUtils.buildEvaluationRequestDto();
        var evaluationWebRequestData =
                evaluationLogMapper.map(evaluationRequestDto, crossValidationConfig);
        assertThat(evaluationWebRequestData.getEvaluationMethod()).isEqualTo(
                evaluationRequestDto.getEvaluationMethod());
        assertThat(evaluationWebRequestData.getInstancesUuid()).isEqualTo(
                evaluationRequestDto.getInstancesUuid());
    }

    @Test
    void testMapCreateEvaluationRequestDtoWithCrossValidationMethod() {
        var evaluationRequestDto = TestHelperUtils.buildEvaluationRequestDto();
        evaluationRequestDto.setEvaluationMethod(EvaluationMethod.CROSS_VALIDATION);
        var evaluationRequestModel =
                evaluationLogMapper.map(evaluationRequestDto, crossValidationConfig);
        assertThat(evaluationRequestModel.getEvaluationMethod()).isEqualTo(
                evaluationRequestDto.getEvaluationMethod());
        assertThat(evaluationRequestModel.getInstancesUuid()).isEqualTo(
                evaluationRequestDto.getInstancesUuid());
        assertThat(evaluationRequestModel.getNumFolds()).isEqualTo(crossValidationConfig.getNumFolds());
        assertThat(evaluationRequestModel.getNumTests()).isEqualTo(crossValidationConfig.getNumTests());
        assertThat(evaluationRequestModel.getSeed()).isEqualTo(crossValidationConfig.getSeed());
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
