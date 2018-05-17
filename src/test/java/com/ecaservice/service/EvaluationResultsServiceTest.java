package com.ecaservice.service;

import com.ecaservice.AssertionUtils;
import com.ecaservice.TestHelperUtils;
import com.ecaservice.dto.evaluation.EvaluationResultsResponse;
import com.ecaservice.dto.evaluation.ResponseStatus;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.entity.EvaluationResultsRequestEntity;
import com.ecaservice.repository.EvaluationLogRepository;
import com.ecaservice.repository.EvaluationResultsRequestRepository;
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
    private EvaluationResultsRequestRepository evaluationResultsRequestRepository;

    private EvaluationResultsService evaluationResultsService;

    private EvaluationResults evaluationResults;

    @Before
    public void init() throws Exception {
        evaluationResultsRequestRepository.deleteAll();
        evaluationLogRepository.deleteAll();
        evaluationResultsService =
                new EvaluationResultsService(evaluationResultsSender, evaluationResultsRequestRepository);
        evaluationResults =
                new EvaluationResults(new KNearestNeighbours(), new Evaluation(TestHelperUtils.loadInstances()));
    }

    @After
    public void after() {
        evaluationResultsRequestRepository.deleteAll();
        evaluationLogRepository.deleteAll();
    }

    @Test
    public void testSuccessSaving() {
        EvaluationLog evaluationLog = TestHelperUtils.createEvaluationLog();
        evaluationLogRepository.save(evaluationLog);
        EvaluationResultsResponse resultsResponse = new EvaluationResultsResponse();
        resultsResponse.setStatus(ResponseStatus.SUCCESS);
        when(evaluationResultsSender.sendEvaluationResults(any(EvaluationResults.class))).thenReturn(resultsResponse);
        evaluationResultsService.saveEvaluationResults(evaluationResults, evaluationLog);
        List<EvaluationResultsRequestEntity> requestEntities = evaluationResultsRequestRepository.findAll();
        AssertionUtils.assertSingletonList(requestEntities);
        EvaluationResultsRequestEntity requestEntity = requestEntities.stream().findFirst().orElse(null);
        Assertions.assertThat(requestEntity).isNotNull();
        Assertions.assertThat(requestEntity.getResponseStatus()).isEqualTo(resultsResponse.getStatus());
        Assertions.assertThat(requestEntity.getEvaluationLog()).isNotNull();
        Assertions.assertThat(requestEntity.getEvaluationLog().getId()).isEqualTo(evaluationLog.getId());
    }

    @Test
    public void testErrorSending() {
        EvaluationLog evaluationLog = TestHelperUtils.createEvaluationLog();
        evaluationLogRepository.save(evaluationLog);
        EvaluationResultsResponse resultsResponse = new EvaluationResultsResponse();
        resultsResponse.setStatus(ResponseStatus.ERROR);
        when(evaluationResultsSender.sendEvaluationResults(any(EvaluationResults.class))).thenThrow(
                new WebServiceIOException("I/O exception"));
        evaluationResultsService.saveEvaluationResults(evaluationResults, evaluationLog);
        List<EvaluationResultsRequestEntity> requestEntities = evaluationResultsRequestRepository.findAll();
        AssertionUtils.assertSingletonList(requestEntities);
        EvaluationResultsRequestEntity requestEntity = requestEntities.stream().findFirst().orElse(null);
        Assertions.assertThat(requestEntity).isNotNull();
        Assertions.assertThat(requestEntity.getResponseStatus()).isEqualTo(ResponseStatus.ERROR);
        Assertions.assertThat(requestEntity.getEvaluationLog()).isNotNull();
        Assertions.assertThat(requestEntity.getEvaluationLog().getId()).isEqualTo(evaluationLog.getId());
        Assertions.assertThat(requestEntity.getRequestId()).isNull();
    }
}
