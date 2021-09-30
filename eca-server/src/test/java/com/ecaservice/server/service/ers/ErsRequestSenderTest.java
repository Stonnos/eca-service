package com.ecaservice.server.service.ers;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.ers.dto.EvaluationResultsRequest;
import com.ecaservice.ers.dto.EvaluationResultsResponse;
import com.ecaservice.server.service.evaluation.EvaluationResultsService;
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

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link ErsRequestSender} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(MockitoExtension.class)
class ErsRequestSenderTest {

    @Mock
    private ErsClient ersClient;
    @Mock
    private EvaluationResultsService evaluationResultsService;

    @InjectMocks
    private ErsRequestSender ersRequestSender;

    private EvaluationResults evaluationResults;

    @BeforeEach
    void init() throws Exception {
        evaluationResults =
                new EvaluationResults(new KNearestNeighbours(), new Evaluation(TestHelperUtils.loadInstances()));
        when(evaluationResultsService.proceed(any(EvaluationResults.class))).thenReturn(new EvaluationResultsRequest());
    }

    @Test
    void testSuccessSending() {
        EvaluationResultsResponse expectedResponse = new EvaluationResultsResponse();
        expectedResponse.setRequestId(UUID.randomUUID().toString());
        when(ersClient.save(any(EvaluationResultsRequest.class))).thenReturn(expectedResponse);
        EvaluationResultsResponse actualResponse =
                ersRequestSender.sendEvaluationResults(evaluationResults, UUID.randomUUID().toString());
        Assertions.assertThat(actualResponse.getRequestId()).isEqualTo(expectedResponse.getRequestId());
    }
}
