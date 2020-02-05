package com.ecaservice.service.ers;

import com.ecaservice.AssertionUtils;
import com.ecaservice.TestHelperUtils;
import com.ecaservice.config.ws.ers.ErsConfig;
import com.ecaservice.dto.evaluation.EvaluationResultsResponse;
import com.ecaservice.dto.evaluation.GetEvaluationResultsRequest;
import com.ecaservice.dto.evaluation.GetEvaluationResultsResponse;
import com.ecaservice.dto.evaluation.ResponseStatus;
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
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.context.annotation.Import;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ws.client.WebServiceIOException;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link ErsRequestService} functionality.
 *
 * @author Roman Batygin
 */
@Import({ErsConfig.class, ClassifierReportMapperImpl.class, ErsResponseStatusMapperImpl.class})
public class ErsRequestServiceTest extends AbstractJpaTest {

    @Inject
    private EvaluationLogRepository evaluationLogRepository;
    @Mock
    private ErsWebServiceClient ersWebServiceClient;
    @Inject
    private ClassifierOptionsRequestModelRepository classifierOptionsRequestModelRepository;
    @Inject
    private ClassifierReportMapper classifierReportMapper;
    @Inject
    private ErsResponseStatusMapper ersResponseStatusMapper;
    @Inject
    private ErsRequestRepository ersRequestRepository;
    @Inject
    private ErsConfig ersConfig;

    private ErsRequestService ersRequestService;

    private EvaluationResults evaluationResults;

    @Override
    public void init() throws Exception {
        ersRequestService = new ErsRequestService(ersWebServiceClient, ersRequestRepository,
                classifierOptionsRequestModelRepository, classifierReportMapper, ersResponseStatusMapper, ersConfig);
        evaluationResults =
                new EvaluationResults(new KNearestNeighbours(), new Evaluation(TestHelperUtils.loadInstances()));
    }

    @Override
    public void deleteAll() {
        ersRequestRepository.deleteAll();
        evaluationLogRepository.deleteAll();
    }

    @Test
    public void testErsDisabled() {
        ReflectionTestUtils.setField(ersRequestService, "ersConfig", new ErsConfig());
        ersRequestService.saveEvaluationResults(evaluationResults, new EvaluationResultsRequestEntity());
        List<ErsRequest> requestEntities = ersRequestRepository.findAll();
        Assertions.assertThat(requestEntities).isNullOrEmpty();
    }

    @Test
    public void testSuccessSaving() {
        EvaluationLog evaluationLog = TestHelperUtils.createEvaluationLog();
        evaluationLogRepository.save(evaluationLog);
        EvaluationResultsResponse resultsResponse = new EvaluationResultsResponse();
        resultsResponse.setStatus(ResponseStatus.SUCCESS);
        when(ersWebServiceClient.sendEvaluationResults(any(EvaluationResults.class), anyString())).thenReturn(
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
    public void testErrorSending() {
        EvaluationLog evaluationLog = TestHelperUtils.createEvaluationLog();
        evaluationLogRepository.save(evaluationLog);
        EvaluationResultsResponse resultsResponse = new EvaluationResultsResponse();
        resultsResponse.setStatus(ResponseStatus.ERROR);
        when(ersWebServiceClient.sendEvaluationResults(any(EvaluationResults.class), anyString())).thenThrow(
                new WebServiceIOException("I/O exception"));
        EvaluationResultsRequestEntity requestEntity = new EvaluationResultsRequestEntity();
        requestEntity.setEvaluationLog(evaluationLog);
        ersRequestService.saveEvaluationResults(evaluationResults, requestEntity);
        List<ErsRequest> requestEntities = ersRequestRepository.findAll();
        AssertionUtils.hasOneElement(requestEntities);
        ErsRequest ersRequest = requestEntities.stream().findFirst().orElse(null);
        Assertions.assertThat(ersRequest).isNotNull();
        Assertions.assertThat(ersRequest.getResponseStatus()).isEqualTo(ErsResponseStatus.ERROR);
        Assertions.assertThat(ersRequest).isInstanceOf(EvaluationResultsRequestEntity.class);
        EvaluationResultsRequestEntity actual = (EvaluationResultsRequestEntity) ersRequest;
        Assertions.assertThat(actual.getEvaluationLog()).isNotNull();
        Assertions.assertThat(actual.getEvaluationLog().getId()).isEqualTo(evaluationLog.getId());
    }

    @Test
    public void testGetEvaluationResults() {
        when(ersWebServiceClient.getEvaluationResultsSimpleResponse(
                any(GetEvaluationResultsRequest.class))).thenReturn(new GetEvaluationResultsResponse());
        GetEvaluationResultsResponse response =
                ersRequestService.getEvaluationResults(UUID.randomUUID().toString());
        Assertions.assertThat(response).isNotNull();
    }
}
