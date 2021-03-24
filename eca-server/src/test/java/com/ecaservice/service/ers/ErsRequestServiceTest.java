package com.ecaservice.service.ers;

import com.ecaservice.AssertionUtils;
import com.ecaservice.TestHelperUtils;
import com.ecaservice.ers.dto.EvaluationResultsResponse;
import com.ecaservice.ers.dto.GetEvaluationResultsRequest;
import com.ecaservice.ers.dto.GetEvaluationResultsResponse;
import com.ecaservice.ers.dto.ResponseStatus;
import com.ecaservice.mapping.ClassifierReportMapper;
import com.ecaservice.mapping.ClassifierReportMapperImpl;
import com.ecaservice.mapping.ErsResponseStatusMapper;
import com.ecaservice.mapping.ErsResponseStatusMapperImpl;
import com.ecaservice.model.entity.ErsRequest;
import com.ecaservice.model.entity.ErsResponseStatus;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.entity.EvaluationResultsRequestEntity;
import com.ecaservice.repository.ClassifierOptionsRequestModelRepository;
import com.ecaservice.repository.ErsRequestRepository;
import com.ecaservice.repository.EvaluationLogRepository;
import com.ecaservice.service.AbstractJpaTest;
import eca.core.evaluation.Evaluation;
import eca.core.evaluation.EvaluationResults;
import eca.metrics.KNearestNeighbours;
import feign.FeignException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link ErsRequestService} functionality.
 *
 * @author Roman Batygin
 */
@Import({ClassifierReportMapperImpl.class, ErsResponseStatusMapperImpl.class})
class ErsRequestServiceTest extends AbstractJpaTest {

    @Inject
    private EvaluationLogRepository evaluationLogRepository;
    @Mock
    private ErsRequestSender ersRequestSender;
    @Inject
    private ClassifierOptionsRequestModelRepository classifierOptionsRequestModelRepository;
    @Inject
    private ClassifierReportMapper classifierReportMapper;
    @Inject
    private ErsResponseStatusMapper ersResponseStatusMapper;
    @Inject
    private ErsRequestRepository ersRequestRepository;

    private ErsRequestService ersRequestService;

    private EvaluationResults evaluationResults;

    @Override
    public void init() throws Exception {
        ersRequestService = new ErsRequestService(ersRequestSender, ersRequestRepository,
                classifierOptionsRequestModelRepository, classifierReportMapper, ersResponseStatusMapper);
        evaluationResults =
                new EvaluationResults(new KNearestNeighbours(), new Evaluation(TestHelperUtils.loadInstances()));
    }

    @Override
    public void deleteAll() {
        ersRequestRepository.deleteAll();
        evaluationLogRepository.deleteAll();
    }

    @Test
    void testSuccessSaving() {
        EvaluationLog evaluationLog = TestHelperUtils.createEvaluationLog();
        evaluationLogRepository.save(evaluationLog);
        EvaluationResultsResponse resultsResponse = new EvaluationResultsResponse();
        resultsResponse.setStatus(ResponseStatus.SUCCESS);
        when(ersRequestSender.sendEvaluationResults(any(EvaluationResults.class), anyString())).thenReturn(
                resultsResponse);
        EvaluationResultsRequestEntity requestEntity = new EvaluationResultsRequestEntity();
        requestEntity.setEvaluationLog(evaluationLog);
        ersRequestService.saveEvaluationResults(evaluationResults, requestEntity);
        List<ErsRequest> requestEntities = ersRequestRepository.findAll();
        AssertionUtils.hasOneElement(requestEntities);
        ErsRequest ersRequest = requestEntities.stream().findFirst().orElse(null);
        Assertions.assertThat(ersRequest).isNotNull();
        Assertions.assertThat(ersRequest.getResponseStatus()).isEqualTo(
                ersResponseStatusMapper.map(resultsResponse.getStatus()));
        Assertions.assertThat(ersRequest).isInstanceOf(EvaluationResultsRequestEntity.class);
        EvaluationResultsRequestEntity actual = (EvaluationResultsRequestEntity) ersRequest;
        Assertions.assertThat(actual.getEvaluationLog()).isNotNull();
        Assertions.assertThat(actual.getEvaluationLog().getId()).isEqualTo(evaluationLog.getId());
    }

    @Test
    void testSendingWithServiceUnavailable() {
        FeignException.ServiceUnavailable serviceUnavailable = mock(FeignException.ServiceUnavailable.class);
        internalTestErrorStatus(serviceUnavailable, ErsResponseStatus.SERVICE_UNAVAILABLE);
    }

    @Test
    void testSendingWithSErrorStatus() {
        FeignException.BadRequest badRequest = mock(FeignException.BadRequest.class);
        internalTestErrorStatus(badRequest, ErsResponseStatus.ERROR);
    }

    @Test
    void testGetEvaluationResults() {
        when(ersRequestSender.getEvaluationResultsSimpleResponse(
                any(GetEvaluationResultsRequest.class))).thenReturn(new GetEvaluationResultsResponse());
        GetEvaluationResultsResponse response =
                ersRequestService.getEvaluationResults(UUID.randomUUID().toString());
        Assertions.assertThat(response).isNotNull();
    }

    private void internalTestErrorStatus(Exception ex, ErsResponseStatus expectedStatus) {
        EvaluationLog evaluationLog = TestHelperUtils.createEvaluationLog();
        evaluationLogRepository.save(evaluationLog);
        when(ersRequestSender.sendEvaluationResults(any(EvaluationResults.class), anyString())).thenThrow(ex);
        EvaluationResultsRequestEntity requestEntity = new EvaluationResultsRequestEntity();
        requestEntity.setEvaluationLog(evaluationLog);
        ersRequestService.saveEvaluationResults(evaluationResults, requestEntity);
        List<ErsRequest> requestEntities = ersRequestRepository.findAll();
        AssertionUtils.hasOneElement(requestEntities);
        ErsRequest ersRequest = requestEntities.stream().findFirst().orElse(null);
        Assertions.assertThat(ersRequest).isNotNull();
        Assertions.assertThat(ersRequest.getResponseStatus()).isEqualTo(expectedStatus);
        Assertions.assertThat(ersRequest).isInstanceOf(EvaluationResultsRequestEntity.class);
        EvaluationResultsRequestEntity actual = (EvaluationResultsRequestEntity) ersRequest;
        Assertions.assertThat(actual.getEvaluationLog()).isNotNull();
        Assertions.assertThat(actual.getEvaluationLog().getId()).isEqualTo(evaluationLog.getId());
    }
}
