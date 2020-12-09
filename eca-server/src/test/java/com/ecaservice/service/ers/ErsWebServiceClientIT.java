package com.ecaservice.service.ers;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.classifier.options.config.ClassifiersOptionsConfiguration;
import com.ecaservice.config.CrossValidationConfig;
import com.ecaservice.config.ws.ers.ErsConfig;
import com.ecaservice.configuation.ErsWebServiceConfiguration;
import com.ecaservice.dto.evaluation.ClassifierOptionsRequest;
import com.ecaservice.dto.evaluation.ClassifierOptionsResponse;
import com.ecaservice.dto.evaluation.EvaluationMethod;
import com.ecaservice.dto.evaluation.EvaluationMethodReport;
import com.ecaservice.dto.evaluation.EvaluationResultsResponse;
import com.ecaservice.dto.evaluation.GetEvaluationResultsRequest;
import com.ecaservice.dto.evaluation.GetEvaluationResultsResponse;
import com.ecaservice.dto.evaluation.ResponseStatus;
import com.ecaservice.mapping.InstancesConverter;
import com.ecaservice.service.evaluation.EvaluationResultsService;
import com.ecaservice.util.Utils;
import eca.core.evaluation.EvaluationResults;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.UUID;

/**
 * Integration tests for checking {@link ErsWebServiceClient} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties
@TestPropertySource("classpath:application.properties")
@Import({ErsWebServiceConfiguration.class, EvaluationResultsService.class,
        ErsConfig.class, ErsWebServiceClient.class, CrossValidationConfig.class,
        ClassifiersOptionsConfiguration.class, InstancesConverter.class})
class ErsWebServiceClientIT {

    @Inject
    private InstancesConverter instancesConverter;
    @Inject
    private ErsWebServiceClient ersWebServiceClient;

    private EvaluationResults evaluationResults;

    @BeforeEach
    void init() {
        evaluationResults = TestHelperUtils.getEvaluationResults();
    }

    @Test
    void testEvaluationResultsSending() {
        String requestId = UUID.randomUUID().toString();
        EvaluationResultsResponse resultsResponse =
                ersWebServiceClient.sendEvaluationResults(evaluationResults, requestId);
        Assertions.assertThat(resultsResponse).isNotNull();
        Assertions.assertThat(resultsResponse.getRequestId()).isEqualTo(requestId);
        Assertions.assertThat(resultsResponse.getStatus()).isEqualTo(ResponseStatus.SUCCESS);
    }

    @Test
    void testGetEvaluationResults() {
        String requestId = UUID.randomUUID().toString();
        EvaluationResultsResponse resultsResponse =
                ersWebServiceClient.sendEvaluationResults(evaluationResults, requestId);
        Assertions.assertThat(resultsResponse).isNotNull();
        GetEvaluationResultsRequest request = new GetEvaluationResultsRequest();
        request.setRequestId(requestId);
        GetEvaluationResultsResponse response = ersWebServiceClient.getEvaluationResultsSimpleResponse(request);
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatus()).isEqualTo(ResponseStatus.SUCCESS);
        Assertions.assertThat(response.getRequestId()).isEqualTo(requestId);
        Assertions.assertThat(response.getClassifierReport()).isNotNull();
        Assertions.assertThat(response.getEvaluationMethodReport()).isNotNull();
        Assertions.assertThat(response.getStatistics()).isNotNull();
    }

    @Test
    void testGetClassifiersOptions() {
        String requestId = UUID.randomUUID().toString();
        EvaluationResultsResponse resultsResponse =
                ersWebServiceClient.sendEvaluationResults(evaluationResults, requestId);
        Assertions.assertThat(resultsResponse).isNotNull();
        //Build classifier options request
        ClassifierOptionsRequest request = new ClassifierOptionsRequest();
        request.setEvaluationMethodReport(new EvaluationMethodReport());
        request.getEvaluationMethodReport().setEvaluationMethod(EvaluationMethod.CROSS_VALIDATION);
        request.getEvaluationMethodReport().setNumFolds(
                BigInteger.valueOf(evaluationResults.getEvaluation().numFolds()));
        request.getEvaluationMethodReport().setNumTests(
                BigInteger.valueOf(evaluationResults.getEvaluation().getValidationsNum()));
        request.getEvaluationMethodReport().setSeed(BigInteger.valueOf(TestHelperUtils.SEED));
        request.setInstances(instancesConverter.convert(evaluationResults.getEvaluation().getData()));
        request.getInstances().setXmlInstances(Utils.toXmlInstances(evaluationResults.getEvaluation().getData()));
        ClassifierOptionsResponse response = ersWebServiceClient.getClassifierOptions(request);
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getRequestId()).isNotNull();
        Assertions.assertThat(response.getStatus()).isEqualTo(ResponseStatus.SUCCESS);
        Assertions.assertThat(response.getClassifierReports()).isNotEmpty();
    }
}
