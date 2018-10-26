package com.ecaservice.service.ers;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.config.CrossValidationConfig;
import com.ecaservice.config.ErsConfig;
import com.ecaservice.configuation.ClassifierOptionsMapperConfiguration;
import com.ecaservice.configuation.ErsWebServiceConfiguration;
import com.ecaservice.dto.evaluation.EvaluationResultsResponse;
import com.ecaservice.dto.evaluation.ResponseStatus;
import com.ecaservice.mapping.EvaluationResultsMapperImpl;
import com.ecaservice.mapping.InstancesConverter;
import com.ecaservice.service.ClassifierOptionsService;
import eca.core.evaluation.EvaluationResults;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.util.UUID;

/**
 * Integration tests for checking {@link ErsWebServiceClient} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@EnableConfigurationProperties
@TestPropertySource("classpath:application.properties")
@Import({ErsWebServiceConfiguration.class, EvaluationResultsMapperImpl.class,
        ErsConfig.class, ErsWebServiceClient.class, CrossValidationConfig.class,
        ClassifierOptionsService.class, ClassifierOptionsMapperConfiguration.class, InstancesConverter.class})
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
