package com.ecaservice.service.ers;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.classifier.options.config.ClassifiersOptionsConfiguration;
import com.ecaservice.config.CrossValidationConfig;
import com.ecaservice.config.ers.ErsConfig;
import com.ecaservice.mapping.InstancesConverter;
import com.ecaservice.service.evaluation.EvaluationResultsService;
import eca.core.evaluation.EvaluationResults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

/**
 * Integration tests for checking {@link ErsRequestSender} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties
@TestPropertySource("classpath:application.properties")
@Import({EvaluationResultsService.class, ErsConfig.class, ErsRequestSender.class, CrossValidationConfig.class,
        ClassifiersOptionsConfiguration.class, InstancesConverter.class})
class ErsRequestSenderIT {

    @Inject
    private InstancesConverter instancesConverter;
    @Inject
    private ErsRequestSender ersRequestSender;

    private EvaluationResults evaluationResults;

    @BeforeEach
    void init() {
        evaluationResults = TestHelperUtils.getEvaluationResults();
    }

   /* @Test
    void testEvaluationResultsSending() {
        String requestId = UUID.randomUUID().toString();
        EvaluationResultsResponse resultsResponse =
                ersRequestSender.sendEvaluationResults(evaluationResults, requestId);
        Assertions.assertThat(resultsResponse).isNotNull();
        Assertions.assertThat(resultsResponse.getRequestId()).isEqualTo(requestId);
        Assertions.assertThat(resultsResponse.getStatus()).isEqualTo(ResponseStatus.SUCCESS);
    }

    @Test
    void testGetEvaluationResults() {
        String requestId = UUID.randomUUID().toString();
        EvaluationResultsResponse resultsResponse =
                ersRequestSender.sendEvaluationResults(evaluationResults, requestId);
        Assertions.assertThat(resultsResponse).isNotNull();
        GetEvaluationResultsRequest request = new GetEvaluationResultsRequest();
        request.setRequestId(requestId);
        GetEvaluationResultsResponse response = ersRequestSender.getEvaluationResultsSimpleResponse(request);
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
                ersRequestSender.sendEvaluationResults(evaluationResults, requestId);
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
        ClassifierOptionsResponse response = ersRequestSender.getClassifierOptions(request);
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getRequestId()).isNotNull();
        Assertions.assertThat(response.getStatus()).isEqualTo(ResponseStatus.SUCCESS);
        Assertions.assertThat(response.getClassifierReports()).isNotEmpty();
    }*/
}
