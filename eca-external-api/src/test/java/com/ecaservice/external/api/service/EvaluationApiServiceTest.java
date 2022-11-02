package com.ecaservice.external.api.service;

import com.ecaservice.base.model.EvaluationRequest;
import com.ecaservice.base.model.ExperimentRequest;
import com.ecaservice.base.model.ExperimentType;
import com.ecaservice.base.model.InstancesRequest;
import com.ecaservice.classifier.options.config.ClassifiersOptionsAutoConfiguration;
import com.ecaservice.external.api.AbstractJpaTest;
import com.ecaservice.external.api.dto.EvaluationRequestDto;
import com.ecaservice.external.api.dto.InstancesRequestDto;
import com.ecaservice.external.api.entity.EvaluationRequestEntity;
import com.ecaservice.external.api.entity.RequestStageType;
import com.ecaservice.external.api.mapping.EcaRequestMapperImpl;
import com.ecaservice.external.api.repository.EvaluationRequestRepository;
import com.ecaservice.external.api.repository.ExperimentRequestRepository;
import eca.regression.Logistic;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import weka.core.Instances;

import javax.inject.Inject;
import java.util.UUID;

import static com.ecaservice.external.api.TestHelperUtils.createEvaluationRequestDto;
import static com.ecaservice.external.api.TestHelperUtils.createEvaluationRequestEntity;
import static com.ecaservice.external.api.TestHelperUtils.createExperimentRequestDto;
import static com.ecaservice.external.api.TestHelperUtils.createExperimentRequestEntity;
import static com.ecaservice.external.api.TestHelperUtils.createInstancesRequestDto;
import static com.ecaservice.external.api.TestHelperUtils.loadInstances;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link EvaluationApiService} functionality.
 *
 * @author Roman Batygin
 */
@Import({EvaluationApiService.class, ClassifiersOptionsAutoConfiguration.class, EcaRequestMapperImpl.class})
class EvaluationApiServiceTest extends AbstractJpaTest {

    @MockBean
    private InstancesService instancesService;
    @MockBean
    private RabbitSender rabbitSender;

    @Inject
    private EvaluationRequestRepository evaluationRequestRepository;
    @Inject
    private ExperimentRequestRepository experimentRequestRepository;
    @Inject
    private EvaluationApiService evaluationApiService;

    @Captor
    private ArgumentCaptor<EvaluationRequest> evaluationRequestArgumentCaptor;
    @Captor
    private ArgumentCaptor<InstancesRequest> instancesRequestArgumentCaptor;
    @Captor
    private ArgumentCaptor<ExperimentRequest> experimentRequestArgumentCaptor;
    @Captor
    private ArgumentCaptor<String> correlationIdCaptor;

    private Instances testInstances;

    @Override
    public void init() {
        testInstances = loadInstances();
        when(instancesService.loadInstances(anyString())).thenReturn(testInstances);
    }

    @Override
    public void deleteAll() {
        evaluationRequestRepository.deleteAll();
    }

    @Test
    void testProcessRequestEvaluationRequest() {
        EvaluationRequestDto evaluationRequestDto = createEvaluationRequestDto();
        EvaluationRequestEntity evaluationRequestEntity = createEvaluationRequestEntity(UUID.randomUUID().toString());
        evaluationRequestRepository.save(evaluationRequestEntity);
        evaluationApiService.processRequest(evaluationRequestEntity, evaluationRequestDto);
        EvaluationRequestEntity actual =
                evaluationRequestRepository.findById(evaluationRequestEntity.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getRequestStage()).isEqualTo(RequestStageType.REQUEST_SENT);
        assertThat(actual.getRequestDate()).isNotNull();
        verify(rabbitSender).sendEvaluationRequest(evaluationRequestArgumentCaptor.capture(),
                correlationIdCaptor.capture());
        assertThat(correlationIdCaptor.getValue()).isEqualTo(evaluationRequestEntity.getCorrelationId());
        EvaluationRequest evaluationRequest = evaluationRequestArgumentCaptor.getValue();
        assertThat(evaluationRequest).isNotNull();
        assertThat(evaluationRequest.getEvaluationMethod()).isEqualTo(evaluationRequestDto.getEvaluationMethod());
        assertThat(evaluationRequest.getNumFolds()).isEqualTo(evaluationRequestDto.getNumFolds());
        assertThat(evaluationRequest.getNumTests()).isEqualTo(evaluationRequestDto.getNumTests());
        assertThat(evaluationRequest.getSeed()).isEqualTo(evaluationRequestDto.getSeed());
        assertThat(evaluationRequest.getData()).isNotNull();
        assertThat(evaluationRequest.getData().relationName()).isEqualTo(testInstances.relationName());
        assertThat(evaluationRequest.getData().numInstances()).isEqualTo(testInstances.numInstances());
        assertThat(evaluationRequest.getClassifier()).isNotNull();
        assertThat(evaluationRequest.getClassifier()).isInstanceOf(Logistic.class);
    }

    @Test
    void testProcessInstancesRequest() {
        InstancesRequestDto instancesRequestDto = createInstancesRequestDto();
        EvaluationRequestEntity evaluationRequestEntity = createEvaluationRequestEntity(UUID.randomUUID().toString());
        evaluationRequestRepository.save(evaluationRequestEntity);
        evaluationApiService.processRequest(evaluationRequestEntity, instancesRequestDto);
        EvaluationRequestEntity actual =
                evaluationRequestRepository.findById(evaluationRequestEntity.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getRequestStage()).isEqualTo(RequestStageType.REQUEST_SENT);
        assertThat(actual.getRequestDate()).isNotNull();
        verify(rabbitSender).sendInstancesRequest(instancesRequestArgumentCaptor.capture(),
                correlationIdCaptor.capture());
        assertThat(correlationIdCaptor.getValue()).isEqualTo(evaluationRequestEntity.getCorrelationId());
        InstancesRequest instancesRequest = instancesRequestArgumentCaptor.getValue();
        assertThat(instancesRequest).isNotNull();
        assertThat(instancesRequest.getData()).isNotNull();
        assertThat(instancesRequest.getData().relationName()).isEqualTo(testInstances.relationName());
        assertThat(instancesRequest.getData().numInstances()).isEqualTo(testInstances.numInstances());
    }

    @Test
    void testProcessExperimentRequest() {
        var experimentRequestDto = createExperimentRequestDto();
        var experimentRequestEntity =
                createExperimentRequestEntity(UUID.randomUUID().toString(), RequestStageType.READY);
        experimentRequestRepository.save(experimentRequestEntity);
        evaluationApiService.processRequest(experimentRequestEntity, experimentRequestDto);
        var actual = experimentRequestRepository.findById(experimentRequestEntity.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getRequestStage()).isEqualTo(RequestStageType.REQUEST_SENT);
        assertThat(actual.getRequestDate()).isNotNull();
        verify(rabbitSender).sendExperimentRequest(experimentRequestArgumentCaptor.capture(),
                correlationIdCaptor.capture());
        assertThat(correlationIdCaptor.getValue()).isEqualTo(experimentRequestEntity.getCorrelationId());
        var experimentRequest = experimentRequestArgumentCaptor.getValue();
        assertThat(experimentRequest).isNotNull();
        assertThat(experimentRequest.getData()).isNotNull();
        assertThat(experimentRequest.getEvaluationMethod()).isEqualTo(experimentRequestDto.getEvaluationMethod());
        assertThat(experimentRequest.getExperimentType()).isEqualTo(ExperimentType.RANDOM_FORESTS);
    }
}
