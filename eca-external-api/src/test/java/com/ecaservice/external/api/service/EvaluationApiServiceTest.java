package com.ecaservice.external.api.service;

import com.ecaservice.base.model.EvaluationRequest;
import com.ecaservice.base.model.ExperimentRequest;
import com.ecaservice.base.model.ExperimentType;
import com.ecaservice.base.model.InstancesRequest;
import com.ecaservice.external.api.AbstractJpaTest;
import com.ecaservice.external.api.config.ExternalApiConfig;
import com.ecaservice.external.api.dto.EvaluationRequestDto;
import com.ecaservice.external.api.dto.InstancesRequestDto;
import com.ecaservice.external.api.entity.EvaluationRequestEntity;
import com.ecaservice.external.api.entity.RequestStageType;
import com.ecaservice.external.api.mapping.EcaRequestMapperImpl;
import com.ecaservice.external.api.repository.EvaluationRequestRepository;
import com.ecaservice.external.api.repository.ExperimentRequestRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import static com.ecaservice.external.api.TestHelperUtils.createEvaluationRequestDto;
import static com.ecaservice.external.api.TestHelperUtils.createExperimentRequestDto;
import static com.ecaservice.external.api.TestHelperUtils.createInstancesRequestDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for checking {@link EvaluationApiService} functionality.
 *
 * @author Roman Batygin
 */
@Import({EvaluationApiService.class, EcaRequestMapperImpl.class,
        EcaRequestService.class, ExternalApiConfig.class, RequestStageHandler.class})
class EvaluationApiServiceTest extends AbstractJpaTest {

    @MockBean
    private RabbitSender rabbitSender;

    @Autowired
    private EvaluationRequestRepository evaluationRequestRepository;
    @Autowired
    private ExperimentRequestRepository experimentRequestRepository;
    @Autowired
    private EvaluationApiService evaluationApiService;

    @Captor
    private ArgumentCaptor<EvaluationRequest> evaluationRequestArgumentCaptor;
    @Captor
    private ArgumentCaptor<InstancesRequest> instancesRequestArgumentCaptor;
    @Captor
    private ArgumentCaptor<ExperimentRequest> experimentRequestArgumentCaptor;
    @Captor
    private ArgumentCaptor<String> correlationIdCaptor;

    @Override
    public void deleteAll() {
        evaluationRequestRepository.deleteAll();
    }

    @Test
    void testProcessRequestEvaluationRequest() {
        EvaluationRequestDto evaluationRequestDto = createEvaluationRequestDto();
        var response = evaluationApiService.processRequest(evaluationRequestDto);
        EvaluationRequestEntity actual =
                evaluationRequestRepository.findByCorrelationId(response.getRequestId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getRequestStage()).isEqualTo(RequestStageType.REQUEST_SENT);
        assertThat(actual.getRequestDate()).isNotNull();
        verify(rabbitSender).sendEvaluationRequest(evaluationRequestArgumentCaptor.capture(),
                correlationIdCaptor.capture());
        assertThat(correlationIdCaptor.getValue()).isEqualTo(response.getRequestId());
        EvaluationRequest evaluationRequest = evaluationRequestArgumentCaptor.getValue();
        assertThat(evaluationRequest).isNotNull();
        assertThat(evaluationRequest.getEvaluationMethod()).isEqualTo(evaluationRequestDto.getEvaluationMethod());
        assertThat(evaluationRequest.getNumFolds()).isEqualTo(evaluationRequestDto.getNumFolds());
        assertThat(evaluationRequest.getNumTests()).isEqualTo(evaluationRequestDto.getNumTests());
        assertThat(evaluationRequest.getSeed()).isEqualTo(evaluationRequestDto.getSeed());
        assertThat(evaluationRequest.getDataUuid()).isEqualTo(evaluationRequestDto.getTrainDataUuid());
        assertThat(evaluationRequest.getClassifierOptions()).isNotNull();
    }

    @Test
    void testProcessInstancesRequest() {
        InstancesRequestDto instancesRequestDto = createInstancesRequestDto();
        var response = evaluationApiService.processRequest(instancesRequestDto);
        EvaluationRequestEntity actual =
                evaluationRequestRepository.findByCorrelationId(response.getRequestId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getRequestStage()).isEqualTo(RequestStageType.REQUEST_SENT);
        assertThat(actual.getRequestDate()).isNotNull();
        verify(rabbitSender).sendInstancesRequest(instancesRequestArgumentCaptor.capture(),
                correlationIdCaptor.capture());
        assertThat(correlationIdCaptor.getValue()).isEqualTo(response.getRequestId());
        InstancesRequest instancesRequest = instancesRequestArgumentCaptor.getValue();
        assertThat(instancesRequest).isNotNull();
        assertThat(instancesRequest.getDataUuid()).isEqualTo(instancesRequestDto.getTrainDataUuid());
    }

    @Test
    void testProcessExperimentRequest() {
        var experimentRequestDto = createExperimentRequestDto();
        var response = evaluationApiService.processRequest(experimentRequestDto);
        var actual =
                experimentRequestRepository.findByCorrelationId(response.getRequestId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getRequestStage()).isEqualTo(RequestStageType.REQUEST_SENT);
        assertThat(actual.getRequestDate()).isNotNull();
        verify(rabbitSender).sendExperimentRequest(experimentRequestArgumentCaptor.capture(),
                correlationIdCaptor.capture());
        assertThat(correlationIdCaptor.getValue()).isEqualTo(response.getRequestId());
        var experimentRequest = experimentRequestArgumentCaptor.getValue();
        assertThat(experimentRequest).isNotNull();
        assertThat(experimentRequest.getDataUuid()).isEqualTo(experimentRequestDto.getTrainDataUuid());
        assertThat(experimentRequest.getEvaluationMethod()).isEqualTo(experimentRequestDto.getEvaluationMethod());
        assertThat(experimentRequest.getExperimentType()).isEqualTo(ExperimentType.RANDOM_FORESTS);
    }
}
