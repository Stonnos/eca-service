package com.ecaservice.service.evaluation;

import com.ecaservice.AssertionUtils;
import com.ecaservice.TestHelperUtils;
import com.ecaservice.config.EvaluationResultsServiceConfig;
import com.ecaservice.dto.evaluation.EvaluationResultsRequest;
import com.ecaservice.dto.evaluation.EvaluationResultsResponse;
import com.ecaservice.dto.evaluation.ResponseStatus;
import com.ecaservice.mapping.EvaluationResultsMapper;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.entity.EvaluationResultsRequestEntity;
import com.ecaservice.repository.EvaluationLogRepository;
import com.ecaservice.repository.EvaluationResultsRequestRepository;
import eca.core.evaluation.Evaluation;
import eca.core.evaluation.EvaluationResults;
import eca.metrics.KNearestNeighbours;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.ws.client.WebServiceIOException;
import org.springframework.ws.client.core.WebServiceTemplate;

import javax.inject.Inject;

import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests fro checking {@link EvaluationResultsSender} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class EvaluationResultsSenderTest {

    private EvaluationResultsSender evaluationResultsSender;

    @Inject
    private EvaluationResultsRequestRepository evaluationResultsRequestRepository;
    @Inject
    private EvaluationLogRepository evaluationLogRepository;
    @Mock
    private EvaluationResultsServiceConfig serviceConfig;
    @Mock
    private WebServiceTemplate webServiceTemplate;
    @Mock
    private EvaluationResultsMapper evaluationResultsMapper;

    private EvaluationResults evaluationResults;

    @Before
    public void init() throws Exception {
        evaluationResultsRequestRepository.deleteAll();
        evaluationLogRepository.deleteAll();
        when(serviceConfig.getEnabled()).thenReturn(true);
        evaluationResults = new EvaluationResults(new KNearestNeighbours(), new Evaluation(TestHelperUtils.loadInstances()));
        when(evaluationResultsMapper.map(evaluationResults)).thenReturn(new EvaluationResultsRequest());
        evaluationResultsSender = new EvaluationResultsSender(webServiceTemplate, evaluationResultsMapper, serviceConfig, evaluationResultsRequestRepository);
    }

    @Test
    public void testEvaluationResultsSendingDisabled() {
        when(serviceConfig.getEnabled()).thenReturn(false);
        evaluationResultsSender.sendEvaluationResults(evaluationResults, new EvaluationLog());
        Assertions.assertThat(evaluationResultsRequestRepository.findAll()).isEmpty();
    }

    @Test
    public void testSuccessSending() {
        EvaluationLog evaluationLog = TestHelperUtils.createEvaluationLog();
        evaluationLogRepository.save(evaluationLog);
        EvaluationResultsResponse resultsResponse = new EvaluationResultsResponse();
        resultsResponse.setStatus(ResponseStatus.SUCCESS);
        when(webServiceTemplate.marshalSendAndReceive(anyString(), any(EvaluationResultsRequest.class))).thenReturn(resultsResponse);
        evaluationResultsSender.sendEvaluationResults(evaluationResults, evaluationLog);
        List<EvaluationResultsRequestEntity> requestEntities = evaluationResultsRequestRepository.findAll();
        AssertionUtils.assertSingletonList(requestEntities);
        EvaluationResultsRequestEntity requestEntity = requestEntities.stream().findFirst().orElse(null);
        Assertions.assertThat(requestEntity.getResponseStatus()).isEqualTo(resultsResponse.getStatus());
    }

    @Test
    public void testErrorSending() {
        EvaluationLog evaluationLog = TestHelperUtils.createEvaluationLog();
        evaluationLogRepository.save(evaluationLog);
        EvaluationResultsResponse resultsResponse = new EvaluationResultsResponse();
        resultsResponse.setStatus(ResponseStatus.ERROR);
        when(webServiceTemplate.marshalSendAndReceive(anyString(), any(EvaluationResultsRequest.class))).thenThrow(new WebServiceIOException("I/O"));
        evaluationResultsSender.sendEvaluationResults(evaluationResults, evaluationLog);
        List<EvaluationResultsRequestEntity> requestEntities = evaluationResultsRequestRepository.findAll();
        AssertionUtils.assertSingletonList(requestEntities);
        EvaluationResultsRequestEntity requestEntity = requestEntities.stream().findFirst().orElse(null);
        Assertions.assertThat(requestEntity.getResponseStatus()).isEqualTo(ResponseStatus.ERROR);
    }
}
