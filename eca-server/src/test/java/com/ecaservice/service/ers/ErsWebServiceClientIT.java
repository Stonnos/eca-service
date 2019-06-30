package com.ecaservice.service.ers;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.config.CrossValidationConfig;
import com.ecaservice.config.ers.ErsConfig;
import com.ecaservice.configuation.ClassifierOptionsMapperConfiguration;
import com.ecaservice.configuation.ErsWebServiceConfiguration;
import com.ecaservice.dto.evaluation.ClassifierOptionsRequest;
import com.ecaservice.dto.evaluation.ClassifierOptionsResponse;
import com.ecaservice.dto.evaluation.EvaluationMethod;
import com.ecaservice.dto.evaluation.EvaluationMethodReport;
import com.ecaservice.dto.evaluation.EvaluationResultsResponse;
import com.ecaservice.dto.evaluation.GetEvaluationResultsRequest;
import com.ecaservice.dto.evaluation.GetEvaluationResultsResponse;
import com.ecaservice.dto.evaluation.InstancesReport;
import com.ecaservice.dto.evaluation.ResponseStatus;
import com.ecaservice.mapping.EvaluationResultsMapperImpl;
import com.ecaservice.mapping.InstancesConverter;
import com.ecaservice.service.ClassifierOptionsService;
import com.ecaservice.util.Utils;
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
import java.math.BigInteger;
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

    @Test
    public void testGetEvaluationResults() {
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
    public void testGetClassifiersOptions() {
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
        request.setInstances(new InstancesReport());
        request.getInstances().setXmlInstances(Utils.toXmlInstances(evaluationResults.getEvaluation().getData()));
        ClassifierOptionsResponse response = ersWebServiceClient.getClassifierOptions(request);
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getRequestId()).isNotNull();
        Assertions.assertThat(response.getStatus()).isEqualTo(ResponseStatus.SUCCESS);
        Assertions.assertThat(response.getClassifierReports()).isNotEmpty();
    }
}
