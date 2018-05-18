package com.ecaservice.service;

import com.ecaservice.AssertionUtils;
import com.ecaservice.TestHelperUtils;
import com.ecaservice.dto.evaluation.EvaluationResultsResponse;
import com.ecaservice.dto.evaluation.ResponseStatus;
import com.ecaservice.model.entity.ErsRequest;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.entity.EvaluationResultsRequestEntity;
import com.ecaservice.repository.ErsRequestRepository;
import com.ecaservice.repository.EvaluationLogRepository;
import eca.core.evaluation.Evaluation;
import eca.core.evaluation.EvaluationResults;
import eca.metrics.KNearestNeighbours;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.ws.client.WebServiceIOException;

import javax.inject.Inject;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link EvaluationResultsService} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class EvaluationResultsServiceTest {

    @Inject
    private EvaluationLogRepository evaluationLogRepository;
    @Mock
    private EvaluationResultsSender evaluationResultsSender;
    @Inject
    private ErsRequestRepository ersRequestRepository;

    private EvaluationResultsService evaluationResultsService;

    private EvaluationResults evaluationResults;

    @Before
    public void init() throws Exception {
        evaluationResultsService = new EvaluationResultsService(evaluationResultsSender, ersRequestRepository);
        evaluationResults =
                new EvaluationResults(new KNearestNeighbours(), new Evaluation(TestHelperUtils.loadInstances()));
    }

    @After
    public void after() {
        ersRequestRepository.deleteAll();
        evaluationLogRepository.deleteAll();
    }

    @Test
    public void testSuccessSaving() {
        EvaluationLog evaluationLog = TestHelperUtils.createEvaluationLog();
        evaluationLogRepository.save(evaluationLog);
        EvaluationResultsResponse resultsResponse = new EvaluationResultsResponse();
        resultsResponse.setStatus(ResponseStatus.SUCCESS);
        when(evaluationResultsSender.sendEvaluationResults(any(EvaluationResults.class), anyString())).thenReturn(
                resultsResponse);
        EvaluationResultsRequestEntity requestEntity = new EvaluationResultsRequestEntity();
        requestEntity.setEvaluationLog(evaluationLog);
        evaluationResultsService.saveEvaluationResults(evaluationResults, requestEntity);
        List<ErsRequest> requestEntities = ersRequestRepository.findAll();
        AssertionUtils.assertSingletonList(requestEntities);
        ErsRequest ersRequest = requestEntities.stream().findFirst().orElse(null);
        Assertions.assertThat(ersRequest).isNotNull();
        Assertions.assertThat(ersRequest.getResponseStatus()).isEqualTo(resultsResponse.getStatus());
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
        when(evaluationResultsSender.sendEvaluationResults(any(EvaluationResults.class), anyString())).thenThrow(
                new WebServiceIOException("I/O exception"));
        EvaluationResultsRequestEntity requestEntity = new EvaluationResultsRequestEntity();
        requestEntity.setEvaluationLog(evaluationLog);
        evaluationResultsService.saveEvaluationResults(evaluationResults, requestEntity);
        List<ErsRequest> requestEntities = ersRequestRepository.findAll();
        AssertionUtils.assertSingletonList(requestEntities);
        ErsRequest ersRequest = requestEntities.stream().findFirst().orElse(null);
        Assertions.assertThat(ersRequest).isNotNull();
        Assertions.assertThat(ersRequest.getResponseStatus()).isEqualTo(ResponseStatus.ERROR);
        Assertions.assertThat(ersRequest).isInstanceOf(EvaluationResultsRequestEntity.class);
        EvaluationResultsRequestEntity actual = (EvaluationResultsRequestEntity) ersRequest;
        Assertions.assertThat(actual.getEvaluationLog()).isNotNull();
        Assertions.assertThat(actual.getEvaluationLog().getId()).isEqualTo(evaluationLog.getId());
    }
}
