package com.ecaservice.service.ers;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.dto.evaluation.EvaluationResultsResponse;
import com.ecaservice.dto.evaluation.ResponseStatus;
import eca.core.evaluation.EvaluationResults;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.util.UUID;

/**
 * Integration tests for checking {@link ErsWebServiceClient} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ErsWebServiceClientIT {

    @Inject
    private ErsWebServiceClient ersWebServiceClient;

    private EvaluationResults evaluationResults;

    @Before
    public void init() throws Exception {
        evaluationResults = TestHelperUtils.getEvaluationResults();
    }

    @Test
    public void testEvaluationResultsSending() {
        String requestId = UUID.randomUUID().toString();
        EvaluationResultsResponse resultsResponse =
                ersWebServiceClient.sendEvaluationResults(evaluationResults, requestId);
        Assertions.assertThat(resultsResponse).isNotNull();
        Assertions.assertThat(resultsResponse.getRequestId()).isEqualTo(requestId);
        Assertions.assertThat(resultsResponse.getStatus()).isEqualTo(ResponseStatus.SUCCESS);
    }
}
