package com.ecaservice.service.evaluation;

import com.ecaservice.AssertionUtils;
import com.ecaservice.TestHelperUtils;
import com.ecaservice.config.CrossValidationConfig;
import com.ecaservice.dto.EvaluationResponse;
import com.ecaservice.dto.evaluation.ClassifierOptionsRequest;
import com.ecaservice.dto.evaluation.ClassifierOptionsResponse;
import com.ecaservice.dto.evaluation.ResponseStatus;
import com.ecaservice.mapping.ClassifierOptionsRequestModelMapper;
import com.ecaservice.mapping.ClassifierReportMapper;
import com.ecaservice.mapping.EvaluationRequestMapper;
import com.ecaservice.model.TechnicalStatus;
import com.ecaservice.model.entity.ClassifierOptionsRequestModel;
import com.ecaservice.model.options.DecisionTreeOptions;
import com.ecaservice.repository.ClassifierOptionsRequestModelRepository;
import com.ecaservice.repository.ErsRequestRepository;
import com.ecaservice.repository.EvaluationLogRepository;
import com.ecaservice.service.ClassifierOptionsService;
import com.ecaservice.service.EvaluationResultsSender;
import com.fasterxml.jackson.databind.ObjectMapper;
import eca.ensemble.forests.DecisionTreeType;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.ws.client.WebServiceIOException;
import weka.core.Instances;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link EvaluationOptimizerService} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class EvaluationOptimizerServiceTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Inject
    private CrossValidationConfig crossValidationConfig;
    @Inject
    private EvaluationRequestService evaluationRequestService;
    @Mock
    private EvaluationResultsSender evaluationResultsSender;
    @Inject
    private ClassifierOptionsRequestModelMapper classifierOptionsRequestModelMapper;
    @Inject
    private ClassifierReportMapper classifierReportMapper;
    @Inject
    private EvaluationRequestMapper evaluationRequestMapper;
    @Inject
    private ClassifierOptionsService classifierOptionsService;
    @Inject
    private ClassifierOptionsRequestModelRepository classifierOptionsRequestModelRepository;
    @Inject
    private ErsRequestRepository ersRequestRepository;
    @Inject
    private EvaluationLogRepository evaluationLogRepository;

    private EvaluationOptimizerService evaluationOptimizerService;

    private Instances data;

    private String classifierOptions;

    @Before
    public void init() throws Exception {
        data = TestHelperUtils.loadInstances();
        evaluationOptimizerService =
                new EvaluationOptimizerService(crossValidationConfig, evaluationRequestService, evaluationResultsSender,
                        classifierOptionsRequestModelMapper, classifierReportMapper, evaluationRequestMapper,
                        classifierOptionsService, classifierOptionsRequestModelRepository);
        DecisionTreeOptions decisionTreeOptions = TestHelperUtils.createDecisionTreeOptions();
        decisionTreeOptions.setDecisionTreeType(DecisionTreeType.CART);
        classifierOptions = objectMapper.writeValueAsString(decisionTreeOptions);
    }

    @After
    public void after() {
        ersRequestRepository.deleteAll();
        evaluationLogRepository.deleteAll();
    }

    @Test
    public void testServiceUnavailable() {
        when(evaluationResultsSender.getClassifierOptions(any(ClassifierOptionsRequest.class))).thenThrow(
                new WebServiceIOException("error"));
        EvaluationResponse evaluationResponse = evaluationOptimizerService.evaluateWithOptimalClassifierOptions(data);
        assertThat(evaluationResponse).isNotNull();
        assertThat(evaluationResponse.getRequestId()).isNotNull();
        assertThat(evaluationResponse.getStatus()).isEqualTo(TechnicalStatus.ERROR);
        assertThat(evaluationResponse.getEvaluationResults()).isNull();
        List<ClassifierOptionsRequestModel> optionsRequests = classifierOptionsRequestModelRepository.findAll();
        AssertionUtils.assertSingletonList(optionsRequests);
        ClassifierOptionsRequestModel requestModel = optionsRequests.get(0);
        assertThat(requestModel.getResponseStatus()).isEqualTo(ResponseStatus.ERROR);
        assertThat(requestModel.getClassifierOptionsResponseModels()).isNullOrEmpty();
    }

    @Test
    public void testsInvalidClassifierOptions() {
        ClassifierOptionsResponse response = TestHelperUtils.createClassifierOptionsResponse(Collections
                .singletonList(TestHelperUtils.createClassifierReport(StringUtils.EMPTY)), ResponseStatus.SUCCESS);
        when(evaluationResultsSender.getClassifierOptions(any(ClassifierOptionsRequest.class))).thenReturn(response);
        EvaluationResponse evaluationResponse = evaluationOptimizerService.evaluateWithOptimalClassifierOptions(data);
        assertThat(evaluationResponse).isNotNull();
        assertThat(evaluationResponse.getRequestId()).isNotNull();
        assertThat(evaluationResponse.getStatus()).isEqualTo(TechnicalStatus.ERROR);
        List<ClassifierOptionsRequestModel> optionsRequests = classifierOptionsRequestModelRepository.findAll();
        AssertionUtils.assertSingletonList(optionsRequests);
        ClassifierOptionsRequestModel requestModel = optionsRequests.get(0);
        assertThat(requestModel.getResponseStatus()).isEqualTo(ResponseStatus.SUCCESS);
    }

    @Test
    public void testEvaluationWithNoClassifierOptionsRequests() {
        ClassifierOptionsResponse response = TestHelperUtils.createClassifierOptionsResponse(Collections
                .singletonList(TestHelperUtils.createClassifierReport(classifierOptions)), ResponseStatus.SUCCESS);
        when(evaluationResultsSender.getClassifierOptions(any(ClassifierOptionsRequest.class))).thenReturn(response);
        EvaluationResponse evaluationResponse = evaluationOptimizerService.evaluateWithOptimalClassifierOptions(data);
        assertThat(evaluationResponse).isNotNull();
        assertThat(evaluationResponse.getRequestId()).isNotNull();
        assertThat(evaluationResponse.getStatus()).isEqualTo(TechnicalStatus.SUCCESS);
        assertThat(evaluationResponse.getEvaluationResults()).isNotNull();
        List<ClassifierOptionsRequestModel> optionsRequests = classifierOptionsRequestModelRepository.findAll();
        AssertionUtils.assertSingletonList(optionsRequests);
        ClassifierOptionsRequestModel requestModel = optionsRequests.get(0);
        assertThat(requestModel.getDataMd5Hash()).isNotNull();
        assertThat(requestModel.getResponseStatus()).isEqualTo(ResponseStatus.SUCCESS);
        assertThat(requestModel.getRequestId()).isNotNull();
        assertThat(requestModel.getNumFolds()).isNotNull();
        assertThat(requestModel.getNumTests()).isNotNull();
        assertThat(requestModel.getSeed()).isNotNull();
        assertThat(requestModel.getEvaluationMethod()).isNotNull();
        assertThat(requestModel.getClassifierOptionsResponseModels()).isNotNull();
        assertThat(requestModel.getClassifierOptionsResponseModels().size()).isOne();
    }
}
