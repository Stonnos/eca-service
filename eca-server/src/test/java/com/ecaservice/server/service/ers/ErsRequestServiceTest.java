package com.ecaservice.server.service.ers;

import com.ecaservice.common.error.model.ValidationErrorDto;
import com.ecaservice.ers.dto.ErsErrorCode;
import com.ecaservice.ers.dto.EvaluationResultsRequest;
import com.ecaservice.ers.dto.EvaluationResultsResponse;
import com.ecaservice.ers.dto.GetEvaluationResultsRequest;
import com.ecaservice.ers.dto.GetEvaluationResultsResponse;
import com.ecaservice.server.AssertionUtils;
import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.mapping.ClassifierReportMapper;
import com.ecaservice.server.mapping.ClassifierReportMapperImpl;
import com.ecaservice.server.mapping.ErsResponseStatusMapper;
import com.ecaservice.server.mapping.ErsResponseStatusMapperImpl;
import com.ecaservice.server.model.ErsEvaluationRequestData;
import com.ecaservice.server.model.entity.ErsRequest;
import com.ecaservice.server.model.entity.ErsResponseStatus;
import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.server.model.entity.EvaluationResultsRequestEntity;
import com.ecaservice.server.repository.AttributesInfoRepository;
import com.ecaservice.server.repository.ClassifierOptionsRequestModelRepository;
import com.ecaservice.server.repository.ErsRequestRepository;
import com.ecaservice.server.repository.EvaluationLogRepository;
import com.ecaservice.server.repository.InstancesInfoRepository;
import com.ecaservice.server.service.AbstractJpaTest;
import com.ecaservice.server.service.evaluation.EvaluationResultsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eca.core.evaluation.Evaluation;
import eca.core.evaluation.EvaluationResults;
import eca.metrics.KNearestNeighbours;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link ErsRequestService} functionality.
 *
 * @author Roman Batygin
 */
@Import({ClassifierReportMapperImpl.class, ErsResponseStatusMapperImpl.class, ErsErrorHandler.class})
class ErsRequestServiceTest extends AbstractJpaTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private EvaluationLogRepository evaluationLogRepository;
    @Mock
    private ErsClient ersClient;
    @Mock
    private ErsRequestSender ersRequestSender;
    @Mock
    private EvaluationResultsService evaluationResultsService;
    @Autowired
    private ClassifierOptionsRequestModelRepository classifierOptionsRequestModelRepository;
    @Autowired
    private ErsErrorHandler ersErrorHandler;
    @Autowired
    private ClassifierReportMapper classifierReportMapper;
    @Autowired
    private ErsResponseStatusMapper ersResponseStatusMapper;
    @Autowired
    private ErsRequestRepository ersRequestRepository;
    @Autowired
    private InstancesInfoRepository instancesInfoRepository;
    @Autowired
    private AttributesInfoRepository attributesInfoRepository;

    private ErsRequestService ersRequestService;

    private EvaluationResults evaluationResults;

    @Override
    public void init() throws Exception {
        ersRequestService =
                new ErsRequestService(ersClient, ersRequestSender, evaluationResultsService, ersRequestRepository,
                        classifierOptionsRequestModelRepository, classifierReportMapper, ersErrorHandler,
                        ersResponseStatusMapper);
        evaluationResults =
                new EvaluationResults(new KNearestNeighbours(), new Evaluation(TestHelperUtils.loadInstances()));
    }

    @Override
    public void deleteAll() {
        ersRequestRepository.deleteAll();
        evaluationLogRepository.deleteAll();
        attributesInfoRepository.deleteAll();
        instancesInfoRepository.deleteAll();
    }

    @Test
    void testSuccessSaving() {
        EvaluationLog evaluationLog = TestHelperUtils.createEvaluationLog();
        instancesInfoRepository.save(evaluationLog.getInstancesInfo());
        evaluationLogRepository.save(evaluationLog);
        EvaluationResultsRequest evaluationResultsRequest = new EvaluationResultsRequest();
        EvaluationResultsResponse resultsResponse = new EvaluationResultsResponse();
        when(ersClient.save(evaluationResultsRequest)).thenReturn(resultsResponse);
        EvaluationResultsRequestEntity requestEntity = new EvaluationResultsRequestEntity();
        requestEntity.setEvaluationLog(evaluationLog);
        var ersEvaluationRequestData = ErsEvaluationRequestData.builder()
                .ersRequest(requestEntity)
                .evaluationEntity(evaluationLog)
                .evaluationResults(evaluationResults)
                .build();
        when(evaluationResultsService.proceed(ersEvaluationRequestData)).thenReturn(evaluationResultsRequest);
        ersRequestService.saveEvaluationResults(ersEvaluationRequestData);
        List<ErsRequest> requestEntities = ersRequestRepository.findAll();
        AssertionUtils.hasOneElement(requestEntities);
        ErsRequest ersRequest = requestEntities.stream().findFirst().orElse(null);
        assertThat(ersRequest).isNotNull();
        assertThat(ersRequest.getResponseStatus()).isEqualTo(ErsResponseStatus.SUCCESS);
        assertThat(ersRequest).isInstanceOf(EvaluationResultsRequestEntity.class);
        EvaluationResultsRequestEntity actual = (EvaluationResultsRequestEntity) ersRequest;
        assertThat(actual.getEvaluationLog()).isNotNull();
        assertThat(actual.getEvaluationLog().getId()).isEqualTo(evaluationLog.getId());
    }

    @Test
    void testSendingWithServiceUnavailable() {
        FeignException.ServiceUnavailable serviceUnavailable = mock(FeignException.ServiceUnavailable.class);
        internalTestErrorStatus(serviceUnavailable, ErsResponseStatus.SERVICE_UNAVAILABLE);
    }

    @Test
    void testSendingWithErrorStatus() {
        FeignException.BadRequest badRequest = mock(FeignException.BadRequest.class);
        internalTestErrorStatus(badRequest, ErsResponseStatus.ERROR);
    }

    @Test
    void testSendingWithBadRequest() throws JsonProcessingException {
        FeignException.BadRequest badRequest = mock(FeignException.BadRequest.class);
        var validationError = new ValidationErrorDto();
        validationError.setCode(ErsErrorCode.DUPLICATE_REQUEST_ID.getCode());
        when(badRequest.contentUTF8()).thenReturn(
                OBJECT_MAPPER.writeValueAsString(Collections.singletonList(validationError)));
        internalTestErrorStatus(badRequest, ErsResponseStatus.DUPLICATE_REQUEST_ID);
    }

    @Test
    void testGetEvaluationResults() {
        when(ersClient.getEvaluationResults(any(GetEvaluationResultsRequest.class)))
                .thenReturn(new GetEvaluationResultsResponse());
        GetEvaluationResultsResponse response =
                ersRequestService.getEvaluationResults(UUID.randomUUID().toString());
        assertThat(response).isNotNull();
    }

    private void internalTestErrorStatus(Exception ex, ErsResponseStatus expectedStatus) {
        EvaluationLog evaluationLog = TestHelperUtils.createEvaluationLog();
        instancesInfoRepository.save(evaluationLog.getInstancesInfo());
        evaluationLogRepository.save(evaluationLog);
        doThrow(ex).when(ersRequestSender).send(any(EvaluationResultsRequest.class));
        EvaluationResultsRequestEntity requestEntity = new EvaluationResultsRequestEntity();
        requestEntity.setEvaluationLog(evaluationLog);
        var ersEvaluationRequestData = ErsEvaluationRequestData.builder()
                .ersRequest(requestEntity)
                .evaluationEntity(evaluationLog)
                .evaluationResults(evaluationResults)
                .build();
        when(evaluationResultsService.proceed(ersEvaluationRequestData)).thenReturn(new EvaluationResultsRequest());
        ersRequestService.saveEvaluationResults(ersEvaluationRequestData);
        List<ErsRequest> requestEntities = ersRequestRepository.findAll();
        AssertionUtils.hasOneElement(requestEntities);
        ErsRequest ersRequest = requestEntities.stream().findFirst().orElse(null);
        assertThat(ersRequest).isNotNull();
        assertThat(ersRequest.getResponseStatus()).isEqualTo(expectedStatus);
        assertThat(ersRequest).isInstanceOf(EvaluationResultsRequestEntity.class);
        EvaluationResultsRequestEntity actual = (EvaluationResultsRequestEntity) ersRequest;
        assertThat(actual.getEvaluationLog()).isNotNull();
        assertThat(actual.getEvaluationLog().getId()).isEqualTo(evaluationLog.getId());
    }
}
