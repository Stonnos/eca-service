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
import com.ecaservice.model.options.ActivationFunctionOptions;
import com.ecaservice.model.options.ClassifierOptions;
import com.ecaservice.model.options.DecisionTreeOptions;
import com.ecaservice.model.options.KNearestNeighboursOptions;
import com.ecaservice.model.options.LogisticOptions;
import com.ecaservice.model.options.NeuralNetworkOptions;
import com.ecaservice.model.options.RandomForestsOptions;
import com.ecaservice.repository.ClassifierOptionsRequestModelRepository;
import com.ecaservice.repository.ErsRequestRepository;
import com.ecaservice.repository.EvaluationLogRepository;
import com.ecaservice.service.ClassifierOptionsService;
import com.ecaservice.service.EvaluationResultsSender;
import com.ecaservice.util.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import eca.core.evaluation.EvaluationResults;
import eca.ensemble.forests.DecisionTreeType;
import eca.ensemble.forests.RandomForests;
import eca.metrics.KNearestNeighbours;
import eca.metrics.distances.DistanceType;
import eca.neural.NeuralNetwork;
import eca.neural.functions.ActivationFunctionType;
import eca.regression.Logistic;
import eca.trees.CART;
import eca.trees.J48;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.DigestUtils;
import org.springframework.ws.client.WebServiceIOException;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

import javax.inject.Inject;
import java.io.IOException;
import java.time.LocalDateTime;
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

    private String decisionTreeOptions;
    private String j48Options;
    private String dataMd5Hash;

    @Before
    public void init() throws Exception {
        data = TestHelperUtils.loadInstances();
        evaluationOptimizerService =
                new EvaluationOptimizerService(crossValidationConfig, evaluationRequestService, evaluationResultsSender,
                        classifierOptionsRequestModelMapper, classifierReportMapper, evaluationRequestMapper,
                        classifierOptionsService, classifierOptionsRequestModelRepository);
        dataMd5Hash = DigestUtils.md5DigestAsHex(Utils.toXmlInstances(data).getBytes());
        DecisionTreeOptions treeOptions = TestHelperUtils.createDecisionTreeOptions();
        treeOptions.setDecisionTreeType(DecisionTreeType.CART);
        decisionTreeOptions = objectMapper.writeValueAsString(treeOptions);
        j48Options = objectMapper.writeValueAsString(TestHelperUtils.createJ48Options());
    }

    @After
    public void after() {
        deleteAll();
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
                .singletonList(TestHelperUtils.createClassifierReport(decisionTreeOptions)), ResponseStatus.SUCCESS);
        when(evaluationResultsSender.getClassifierOptions(any(ClassifierOptionsRequest.class))).thenReturn(response);
        EvaluationResponse evaluationResponse = evaluationOptimizerService.evaluateWithOptimalClassifierOptions(data);
        assertSuccessEvaluationResponse(evaluationResponse);
        List<ClassifierOptionsRequestModel> optionsRequests = classifierOptionsRequestModelRepository.findAll();
        AssertionUtils.assertSingletonList(optionsRequests);
        assertSuccessClassifierOptionsRequestModel(optionsRequests.get(0));
    }

    /**
     * Tests for checking exceeded cache cases:
     * Case 1: It's already been N days after last request
     * Case 2: Last response status is ERROR
     * Case 3: Requests with specified data MD5 hash doesn't exists
     * Case 4: Last response status is SUCCESS, but doesn't contains any classifier options response model
     */
    @Test
    public void testExceededClassifierOptionsCache() {
        //Case 1
        ClassifierOptionsRequestModel requestModel = TestHelperUtils.createClassifierOptionsRequestModel(dataMd5Hash,
                LocalDateTime.now().minusDays(crossValidationConfig.getClassifierOptionsCacheDurationInDays() + 1),
                ResponseStatus.SUCCESS, Collections.emptyList());
        classifierOptionsRequestModelRepository.save(requestModel);
        ClassifierOptionsResponse response = TestHelperUtils.createClassifierOptionsResponse(Collections
                .singletonList(TestHelperUtils.createClassifierReport(decisionTreeOptions)), ResponseStatus.SUCCESS);
        when(evaluationResultsSender.getClassifierOptions(any(ClassifierOptionsRequest.class))).thenReturn(response);
        EvaluationResponse evaluationResponse = evaluationOptimizerService.evaluateWithOptimalClassifierOptions(data);
        assertSuccessEvaluationResponse(evaluationResponse);
        List<ClassifierOptionsRequestModel> optionsRequests = classifierOptionsRequestModelRepository.findAll();
        assertThat(optionsRequests.size()).isEqualTo(2);
        assertSuccessClassifierOptionsRequestModel(optionsRequests.get(1));
        deleteAll();
        //Case 2
        requestModel = TestHelperUtils.createClassifierOptionsRequestModel(dataMd5Hash,
                LocalDateTime.now(), ResponseStatus.ERROR, Collections.emptyList());
        classifierOptionsRequestModelRepository.save(requestModel);
        evaluationResponse = evaluationOptimizerService.evaluateWithOptimalClassifierOptions(data);
        assertSuccessEvaluationResponse(evaluationResponse);
        optionsRequests = classifierOptionsRequestModelRepository.findAll();
        assertThat(optionsRequests.size()).isEqualTo(2);
        assertSuccessClassifierOptionsRequestModel(optionsRequests.get(1));
        deleteAll();
        //Case 3
        requestModel = TestHelperUtils.createClassifierOptionsRequestModel(StringUtils.EMPTY,
                LocalDateTime.now(), ResponseStatus.SUCCESS, Collections.emptyList());
        classifierOptionsRequestModelRepository.save(requestModel);
        evaluationResponse = evaluationOptimizerService.evaluateWithOptimalClassifierOptions(data);
        assertSuccessEvaluationResponse(evaluationResponse);
        optionsRequests = classifierOptionsRequestModelRepository.findAll();
        assertThat(optionsRequests.size()).isEqualTo(2);
        assertSuccessClassifierOptionsRequestModel(optionsRequests.get(1));
        deleteAll();
        //Case 4
        requestModel = TestHelperUtils.createClassifierOptionsRequestModel(dataMd5Hash,
                LocalDateTime.now(), ResponseStatus.SUCCESS, Collections.emptyList());
        classifierOptionsRequestModelRepository.save(requestModel);
        evaluationResponse = evaluationOptimizerService.evaluateWithOptimalClassifierOptions(data);
        assertSuccessEvaluationResponse(evaluationResponse);
        optionsRequests = classifierOptionsRequestModelRepository.findAll();
        assertThat(optionsRequests.size()).isEqualTo(2);
        assertSuccessClassifierOptionsRequestModel(optionsRequests.get(1));
    }

    @Test
    public void testClassifierOptionsCache() {
        ClassifierOptionsRequestModel requestModel =
                TestHelperUtils.createClassifierOptionsRequestModel(dataMd5Hash, LocalDateTime.now().minusDays(1),
                        ResponseStatus.SUCCESS, Collections.singletonList(
                                TestHelperUtils.createClassifierOptionsResponseModel(decisionTreeOptions)));
        ClassifierOptionsRequestModel requestModel1 =
                TestHelperUtils.createClassifierOptionsRequestModel(dataMd5Hash, LocalDateTime.now(),
                        ResponseStatus.SUCCESS,
                        Collections.singletonList(TestHelperUtils.createClassifierOptionsResponseModel(j48Options)));
        classifierOptionsRequestModelRepository.save(requestModel);
        classifierOptionsRequestModelRepository.save(requestModel1);
        EvaluationResponse evaluationResponse = evaluationOptimizerService.evaluateWithOptimalClassifierOptions(data);
        assertSuccessEvaluationResponse(evaluationResponse);
        EvaluationResults results = evaluationResponse.getEvaluationResults();
        assertThat(results.getClassifier()).isInstanceOf(J48.class);
        List<ClassifierOptionsRequestModel> optionsRequests = classifierOptionsRequestModelRepository.findAll();
        assertThat(optionsRequests.size()).isEqualTo(2);
    }

    /**
     * Tests all classifiers evaluation.
     * Case 1: Decision tree CART
     * Case 2: Logistic regression
     * Case 3: KNN classifier
     * Case 4: Neural network
     * Case 5: Random forests
     *
     * @throws IOException
     */
    @Test
    public void testClassifiersEvaluation() throws IOException {
        //Case 1
        DecisionTreeOptions treeOptions = TestHelperUtils.createDecisionTreeOptions();
        treeOptions.setDecisionTreeType(DecisionTreeType.CART);
        performClassifierEvaluationTest(treeOptions, CART.class);
        //Case 2
        performClassifierEvaluationTest(new LogisticOptions(), Logistic.class);
        //Case 3
        KNearestNeighboursOptions kNearestNeighboursOptions = TestHelperUtils.createKNearestNeighboursOptions();
        kNearestNeighboursOptions.setDistanceType(DistanceType.MANHATTAN);
        performClassifierEvaluationTest(kNearestNeighboursOptions, KNearestNeighbours.class);
        //Case 4
        NeuralNetworkOptions networkOptions = TestHelperUtils.createNeuralNetworkOptions();
        networkOptions.setActivationFunctionOptions(new ActivationFunctionOptions());
        networkOptions.getActivationFunctionOptions().setActivationFunctionType(ActivationFunctionType.EXPONENTIAL);
        performClassifierEvaluationTest(networkOptions, NeuralNetwork.class);
        //Case 5
        RandomForestsOptions randomForestsOptions = TestHelperUtils.createRandomForestsOptions(DecisionTreeType.C45);
        //performClassifierEvaluationTest(randomForestsOptions, RandomForests.class);
    }

    private <U extends ClassifierOptions, V extends AbstractClassifier> void performClassifierEvaluationTest(U options,
                                                                                                             Class<V> classifierClazz)
            throws IOException {
        ClassifierOptionsResponse response = TestHelperUtils.createClassifierOptionsResponse(Collections.singletonList(
                TestHelperUtils.createClassifierReport(objectMapper.writeValueAsString(options))),
                ResponseStatus.SUCCESS);
        when(evaluationResultsSender.getClassifierOptions(any(ClassifierOptionsRequest.class))).thenReturn(response);
        EvaluationResponse evaluationResponse = evaluationOptimizerService.evaluateWithOptimalClassifierOptions(data);
        assertSuccessEvaluationResponse(evaluationResponse);
        assertThat(evaluationResponse.getEvaluationResults().getClassifier()).isInstanceOf(classifierClazz);
        deleteAll();
    }

    private void assertSuccessEvaluationResponse(EvaluationResponse evaluationResponse) {
        assertThat(evaluationResponse).isNotNull();
        assertThat(evaluationResponse.getRequestId()).isNotNull();
        assertThat(evaluationResponse.getStatus()).isEqualTo(TechnicalStatus.SUCCESS);
        assertThat(evaluationResponse.getEvaluationResults()).isNotNull();
    }

    private void assertSuccessClassifierOptionsRequestModel(ClassifierOptionsRequestModel requestModel) {
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

    private void deleteAll() {
        ersRequestRepository.deleteAll();
        evaluationLogRepository.deleteAll();
    }
}
