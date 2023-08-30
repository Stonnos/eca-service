package com.ecaservice.server.mapping;

import com.ecaservice.base.model.ExperimentRequest;
import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.config.CrossValidationConfig;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.report.model.ExperimentBean;
import com.ecaservice.web.dto.model.ExperimentDto;
import eca.core.evaluation.EvaluationMethod;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


/**
 * Unit tests that checks ExperimentMapper functionality {@see ExperimentMapper}.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import({ExperimentMapperImpl.class, CrossValidationConfig.class, DateTimeConverter.class,
        InstancesInfoMapperImpl.class})
class ExperimentMapperTest {

    private static final String EXPERIMENT_PATH = "experiment.model";

    @Inject
    private CrossValidationConfig crossValidationConfig;
    @Inject
    private ExperimentMapper experimentMapper;

    @Test
    void testMapExperimentRequestData() {
        ExperimentRequest experimentRequest = TestHelperUtils.createExperimentRequest();
        Message message = Mockito.mock(Message.class);
        MessageProperties messageProperties = TestHelperUtils.buildMessageProperties();
        when(message.getMessageProperties()).thenReturn(messageProperties);
        var experimentMessageRequest = experimentMapper.map(experimentRequest, message);
        assertThat(experimentMessageRequest).isNotNull();
        assertThat(experimentMessageRequest.getEmail()).isEqualTo(experimentRequest.getEmail());
        assertThat(experimentMessageRequest.getExperimentType()).isEqualTo(experimentRequest.getExperimentType());
        assertThat(experimentMessageRequest.getEvaluationMethod()).isEqualTo(experimentRequest.getEvaluationMethod());
        assertThat(experimentMessageRequest.getReplyTo()).isEqualTo(message.getMessageProperties().getReplyTo());
        assertThat(experimentMessageRequest.getCorrelationId()).isEqualTo(
                message.getMessageProperties().getCorrelationId());
    }

    @Test
    void testMapExperimentRequestWithTrainingDataEvaluationMethod() {
        var experimentMessageRequest = TestHelperUtils.createExperimentMessageRequest();
        Experiment experiment = experimentMapper.map(experimentMessageRequest, crossValidationConfig);
        assertThat(experiment).isNotNull();
        assertThat(experiment.getEmail()).isEqualTo(experimentMessageRequest.getEmail());
        assertThat(experiment.getEvaluationMethod()).isEqualTo(experimentMessageRequest.getEvaluationMethod());
        assertThat(experiment.getNumFolds()).isNull();
        assertThat(experiment.getNumTests()).isNull();
        assertThat(experiment.getSeed()).isNull();
        assertThat(experiment.getExperimentType()).isEqualTo(experimentMessageRequest.getExperimentType());
    }

    @Test
    void testMapExperimentRequestWithCrossValidationEvaluationMethod() {
        var experimentMessageRequest = TestHelperUtils.createExperimentMessageRequest();
        experimentMessageRequest.setEvaluationMethod(EvaluationMethod.CROSS_VALIDATION);
        Experiment experiment = experimentMapper.map(experimentMessageRequest, crossValidationConfig);
        assertThat(experiment).isNotNull();
        assertThat(experiment.getEmail()).isEqualTo(experimentMessageRequest.getEmail());
        assertThat(experiment.getEvaluationMethod()).isEqualTo(experimentMessageRequest.getEvaluationMethod());
        assertThat(experiment.getNumFolds()).isEqualTo(crossValidationConfig.getNumFolds());
        assertThat(experiment.getNumTests()).isEqualTo(crossValidationConfig.getNumTests());
        assertThat(experiment.getSeed()).isEqualTo(crossValidationConfig.getSeed());
        assertThat(experiment.getExperimentType()).isEqualTo(experimentMessageRequest.getExperimentType());
    }

    @Test
    void testMapToExperimentDto() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        experiment.setEvaluationMethod(EvaluationMethod.CROSS_VALIDATION);
        experiment.setStartDate(LocalDateTime.now().plusHours(1L));
        experiment.setEndDate(experiment.getStartDate().minusMinutes(1L));
        experiment.setDeletedDate(experiment.getEndDate().plusMinutes(1L));
        experiment.setModelPath(EXPERIMENT_PATH);
        experiment.setNumFolds(crossValidationConfig.getNumFolds());
        experiment.setNumTests(crossValidationConfig.getNumTests());
        experiment.setSeed(crossValidationConfig.getSeed());
        experiment.setMaxPctCorrect(BigDecimal.TEN);
        ExperimentDto experimentDto = experimentMapper.map(experiment);
        assertThat(experimentDto).isNotNull();
        assertThat(experimentDto.getCreatedBy()).isEqualTo(experiment.getCreatedBy());
        assertThat(experimentDto.getCreationDate()).isEqualTo(experiment.getCreationDate());
        assertThat(experimentDto.getStartDate()).isEqualTo(experiment.getStartDate());
        assertThat(experimentDto.getEndDate()).isEqualTo(experiment.getEndDate());
        assertThat(experimentDto.getDeletedDate()).isEqualTo(experiment.getDeletedDate());
        assertThat(experimentDto.getEvaluationMethod().getDescription()).isEqualTo(
                experiment.getEvaluationMethod().getDescription());
        assertThat(experimentDto.getEvaluationMethod().getValue()).isEqualTo(
                experiment.getEvaluationMethod().name());
        assertThat(experimentDto.getRequestStatus().getDescription()).isEqualTo(
                experiment.getRequestStatus().getDescription());
        assertThat(experimentDto.getRequestStatus().getValue()).isEqualTo(
                experiment.getRequestStatus().name());
        assertThat(experimentDto.getExperimentType().getDescription()).isEqualTo(
                experiment.getExperimentType().getDescription());
        assertThat(experimentDto.getExperimentType().getValue()).isEqualTo(
                experiment.getExperimentType().name());
        assertThat(experimentDto.getModelPath()).isEqualTo(experiment.getModelPath());
        assertThat(experimentDto.getRequestId()).isEqualTo(experiment.getRequestId());
        assertThat(experimentDto.getNumFolds()).isEqualTo(experiment.getNumFolds());
        assertThat(experimentDto.getNumTests()).isEqualTo(experiment.getNumTests());
        assertThat(experimentDto.getSeed()).isEqualTo(experiment.getSeed());
        assertThat(experimentDto.getEvaluationTotalTime()).isNotNull();
        assertThat(experimentDto.getInstancesInfo()).isNotNull();
        assertThat(experimentDto.getMaxPctCorrect()).isEqualTo(experiment.getMaxPctCorrect());
    }

    @Test
    void testMapExperimentDtoList() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        Experiment experiment1 = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        List<Experiment> experiments = Arrays.asList(experiment, experiment1);
        List<ExperimentDto> experimentDtoList = experimentMapper.map(experiments);
        assertThat(experimentDtoList).hasSameSizeAs(experiments);
    }

    @Test
    void testMapToExperimentBean() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        experiment.setStartDate(LocalDateTime.now());
        experiment.setEndDate(LocalDateTime.now());
        experiment.setDeletedDate(LocalDateTime.now());
        experiment.setModelPath(EXPERIMENT_PATH);
        experiment.setMaxPctCorrect(BigDecimal.TEN);
        ExperimentBean experimentBean = experimentMapper.mapToBean(experiment);
        assertThat(experimentBean).isNotNull();
        assertThat(experimentBean.getCreatedBy()).isEqualTo(experiment.getCreatedBy());
        assertThat(experimentBean.getCreationDate()).isNotNull();
        assertThat(experimentBean.getStartDate()).isNotNull();
        assertThat(experimentBean.getEndDate()).isNotNull();
        assertThat(experimentBean.getDeletedDate()).isNotNull();
        assertThat(experimentBean.getEvaluationMethod()).isNotNull();
        assertThat(experimentBean.getRequestStatus()).isEqualTo(experiment.getRequestStatus().getDescription());
        assertThat(experimentBean.getExperimentType()).isEqualTo(experiment.getExperimentType().getDescription());
        assertThat(experimentBean.getModelPath()).isEqualTo(experiment.getModelPath());
        assertThat(experimentBean.getRequestId()).isEqualTo(experiment.getRequestId());
        assertThat(experimentBean.getEvaluationTotalTime()).isNotNull();
        assertThat(experimentBean.getRelationName()).isEqualTo(experiment.getInstancesInfo().getRelationName());
        assertThat(experimentBean.getMaxPctCorrect()).isEqualTo(experiment.getMaxPctCorrect());
    }

    @Test
    void testMapToExperimentModel() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        var experimentModel = experimentMapper.mapToModel(experiment);
        assertThat(experimentModel).isNotNull();
        assertThat(experimentModel.getRequestStatus()).isEqualTo(experiment.getRequestStatus().name());
        assertThat(experimentModel.getExperimentType()).isEqualTo(experiment.getExperimentType().name());
        assertThat(experimentModel.getExperimentType()).isEqualTo(experiment.getExperimentType().name());
        assertThat(experimentModel.getRequestId()).isEqualTo(experiment.getRequestId());
        assertThat(experimentModel.getEmail()).isEqualTo(experiment.getEmail());
        assertThat(experimentModel.getChannel()).isEqualTo(experiment.getChannel().name());
    }
}
