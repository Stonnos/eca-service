package com.ecaservice.service;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.config.EvaluationResultsServiceConfig;
import com.ecaservice.dto.evaluation.EvaluationResultsRequest;
import com.ecaservice.dto.evaluation.EvaluationResultsResponse;
import com.ecaservice.dto.evaluation.ResponseStatus;
import com.ecaservice.exception.EcaServiceException;
import com.ecaservice.mapping.EvaluationResultsMapper;
import eca.core.evaluation.Evaluation;
import eca.core.evaluation.EvaluationResults;
import eca.metrics.KNearestNeighbours;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.ws.client.WebServiceIOException;
import org.springframework.ws.client.core.WebServiceTemplate;

import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link EvaluationResultsSender} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class EvaluationResultsSenderTest {

    @InjectMocks
    private EvaluationResultsSender evaluationResultsSender;

    @Mock
    private EvaluationResultsServiceConfig serviceConfig;
    @Mock
    private WebServiceTemplate webServiceTemplate;
    @Mock
    private EvaluationResultsMapper evaluationResultsMapper;

    private EvaluationResults evaluationResults;

    @Before
    public void init() throws Exception {
        when(serviceConfig.getEnabled()).thenReturn(true);
        evaluationResults =
                new EvaluationResults(new KNearestNeighbours(), new Evaluation(TestHelperUtils.loadInstances()));
    }

    @Test(expected = EcaServiceException.class)
    public void testEvaluationResultsSendingDisabled() {
        when(serviceConfig.getEnabled()).thenReturn(false);
        evaluationResultsSender.sendEvaluationResults(evaluationResults);
    }

    @Test
    public void testSuccessSending() {
        EvaluationResultsResponse expectedResponse = new EvaluationResultsResponse();
        expectedResponse.setStatus(ResponseStatus.SUCCESS);
        expectedResponse.setRequestId(UUID.randomUUID().toString());
        when(webServiceTemplate.marshalSendAndReceive(anyString(), any(EvaluationResultsRequest.class))).thenReturn(
                expectedResponse);
        EvaluationResultsResponse actualResponse = evaluationResultsSender.sendEvaluationResults(evaluationResults);
        Assertions.assertThat(actualResponse.getRequestId()).isEqualTo(expectedResponse.getRequestId());
        Assertions.assertThat(actualResponse.getStatus()).isEqualTo(expectedResponse.getStatus());
    }

    @Test(expected = WebServiceIOException.class)
    public void testErrorSending() {
        when(webServiceTemplate.marshalSendAndReceive(anyString(), any(EvaluationResultsRequest.class))).thenThrow(
                new WebServiceIOException("I/O exception"));
        evaluationResultsSender.sendEvaluationResults(evaluationResults);
    }
}
