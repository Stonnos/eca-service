package com.ecaservice.service.ers;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.config.ws.ers.ErsConfig;
import com.ecaservice.dto.evaluation.EvaluationResultsRequest;
import com.ecaservice.dto.evaluation.EvaluationResultsResponse;
import com.ecaservice.dto.evaluation.ResponseStatus;
import com.ecaservice.service.evaluation.EvaluationResultsService;
import eca.core.evaluation.Evaluation;
import eca.core.evaluation.EvaluationResults;
import eca.metrics.KNearestNeighbours;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ws.client.WebServiceIOException;
import org.springframework.ws.client.core.WebServiceTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link ErsWebServiceClient} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(MockitoExtension.class)
public class ErsWebServiceClientTest {

    private static final String WEB_SERVICE_URL = "http://localhost";

    @Mock
    private ErsConfig serviceConfig;
    @Mock
    private WebServiceTemplate ersWebServiceTemplate;
    @Mock
    private EvaluationResultsService evaluationResultsService;

    @InjectMocks
    private ErsWebServiceClient ersWebServiceClient;

    private EvaluationResults evaluationResults;

    @BeforeEach
    public void init() throws Exception {
        evaluationResults =
                new EvaluationResults(new KNearestNeighbours(), new Evaluation(TestHelperUtils.loadInstances()));
        when(evaluationResultsService.proceed(any(EvaluationResults.class))).thenReturn(new EvaluationResultsRequest());
        when(serviceConfig.getUrl()).thenReturn(WEB_SERVICE_URL);
    }

    @Test
    public void testSuccessSending() {
        EvaluationResultsResponse expectedResponse = new EvaluationResultsResponse();
        expectedResponse.setStatus(ResponseStatus.SUCCESS);
        expectedResponse.setRequestId(UUID.randomUUID().toString());
        when(ersWebServiceTemplate.marshalSendAndReceive(anyString(), any(EvaluationResultsRequest.class))).thenReturn(
                expectedResponse);
        EvaluationResultsResponse actualResponse =
                ersWebServiceClient.sendEvaluationResults(evaluationResults, UUID.randomUUID().toString());
        Assertions.assertThat(actualResponse.getRequestId()).isEqualTo(expectedResponse.getRequestId());
        Assertions.assertThat(actualResponse.getStatus()).isEqualTo(expectedResponse.getStatus());
    }

    @Test
    public void testErrorSending() {
        when(ersWebServiceTemplate.marshalSendAndReceive(anyString(), any(EvaluationResultsRequest.class))).thenThrow(
                new WebServiceIOException("I/O exception"));
        assertThrows(WebServiceIOException.class, () -> ersWebServiceClient.sendEvaluationResults(evaluationResults,
                UUID.randomUUID().toString()));
    }
}
