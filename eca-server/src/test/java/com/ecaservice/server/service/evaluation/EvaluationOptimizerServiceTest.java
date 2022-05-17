package com.ecaservice.server.service.evaluation;

import com.ecaservice.base.model.ErrorCode;
import com.ecaservice.base.model.EvaluationResponse;
import com.ecaservice.base.model.InstancesRequest;
import com.ecaservice.base.model.TechnicalStatus;
import com.ecaservice.classifier.options.config.ClassifiersOptionsAutoConfiguration;
import com.ecaservice.classifier.options.model.ActivationFunctionOptions;
import com.ecaservice.classifier.options.model.ClassifierOptions;
import com.ecaservice.classifier.options.model.DecisionTreeOptions;
import com.ecaservice.classifier.options.model.ExtraTreesOptions;
import com.ecaservice.classifier.options.model.KNearestNeighboursOptions;
import com.ecaservice.classifier.options.model.LogisticOptions;
import com.ecaservice.classifier.options.model.NeuralNetworkOptions;
import com.ecaservice.classifier.options.model.RandomForestsOptions;
import com.ecaservice.ers.dto.ClassifierOptionsRequest;
import com.ecaservice.ers.dto.ClassifierOptionsResponse;
import com.ecaservice.server.AssertionUtils;
import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.config.AppProperties;
import com.ecaservice.server.config.CrossValidationConfig;
import com.ecaservice.server.config.ers.ErsConfig;
import com.ecaservice.server.configuation.ExecutorConfiguration;
import com.ecaservice.server.mapping.ClassifierInfoMapperImpl;
import com.ecaservice.server.mapping.ClassifierOptionsRequestMapperImpl;
import com.ecaservice.server.mapping.ClassifierOptionsRequestModelMapperImpl;
import com.ecaservice.server.mapping.ClassifierOptionsResponseModelMapperImpl;
import com.ecaservice.server.mapping.ClassifierReportMapperImpl;
import com.ecaservice.server.mapping.DateTimeConverter;
import com.ecaservice.server.mapping.ErsEvaluationMethodMapperImpl;
import com.ecaservice.server.mapping.ErsResponseStatusMapperImpl;
import com.ecaservice.server.mapping.EvaluationLogMapperImpl;
import com.ecaservice.server.mapping.EvaluationRequestMapperImpl;
import com.ecaservice.server.mapping.InstancesConverter;
import com.ecaservice.server.mapping.InstancesInfoMapperImpl;
import com.ecaservice.server.model.entity.ClassifierOptionsRequestEntity;
import com.ecaservice.server.model.entity.ClassifierOptionsRequestModel;
import com.ecaservice.server.model.entity.ErsResponseStatus;
import com.ecaservice.server.model.evaluation.ClassifierOptionsRequestSource;
import com.ecaservice.server.repository.ClassifierOptionsRequestModelRepository;
import com.ecaservice.server.repository.ClassifierOptionsRequestRepository;
import com.ecaservice.server.repository.ErsRequestRepository;
import com.ecaservice.server.repository.EvaluationLogRepository;
import com.ecaservice.server.service.AbstractJpaTest;
import com.ecaservice.server.service.ers.ErsClient;
import com.ecaservice.server.service.ers.ErsErrorHandler;
import com.ecaservice.server.service.ers.ErsRequestSender;
import com.ecaservice.server.service.ers.ErsRequestService;
import com.ecaservice.server.service.evaluation.initializers.ClassifierInitializerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import eca.core.evaluation.EvaluationResults;
import eca.ensemble.AdaBoostClassifier;
import eca.ensemble.HeterogeneousClassifier;
import eca.ensemble.ModifiedHeterogeneousClassifier;
import eca.ensemble.StackingClassifier;
import eca.ensemble.forests.DecisionTreeType;
import eca.ensemble.forests.ExtraTreesClassifier;
import eca.ensemble.forests.RandomForests;
import eca.metrics.KNearestNeighbours;
import eca.metrics.distances.DistanceType;
import eca.neural.NeuralNetwork;
import eca.neural.functions.ActivationFunctionType;
import eca.regression.Logistic;
import eca.trees.CART;
import eca.trees.J48;
import feign.FeignException;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.DigestUtils;
import weka.classifiers.AbstractClassifier;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static com.ecaservice.server.util.InstancesUtils.toJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link EvaluationOptimizerService} functionality.
 *
 * @author Roman Batygin
 */
@Import({ExecutorConfiguration.class, ClassifiersOptionsAutoConfiguration.class, AppProperties.class,
        CrossValidationConfig.class, EvaluationRequestService.class, InstancesInfoMapperImpl.class,
        ClassifierOptionsRequestModelMapperImpl.class, ClassifierReportMapperImpl.class,
        EvaluationRequestMapperImpl.class, ClassifierOptionsRequestMapperImpl.class,
        ErsConfig.class, EvaluationLogMapperImpl.class,
        EvaluationService.class, ErsEvaluationMethodMapperImpl.class, ErsResponseStatusMapperImpl.class,
        InstancesConverter.class, ClassifierOptionsResponseModelMapperImpl.class, ErsRequestService.class,
        EvaluationOptimizerService.class, ClassifierInfoMapperImpl.class, ErsErrorHandler.class,
        ClassifierOptionsCacheService.class, DateTimeConverter.class})
class EvaluationOptimizerServiceTest extends AbstractJpaTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private ErsClient ersClient;
    @MockBean
    private ErsRequestSender ersRequestSender;
    @MockBean
    private EvaluationResultsService evaluationResultsService;
    @MockBean
    private ClassifierInitializerService classifierInitializerService;
    @Inject
    private ErsConfig ersConfig;
    @Inject
    private ClassifierOptionsRequestModelRepository classifierOptionsRequestModelRepository;
    @Inject
    private ErsRequestRepository ersRequestRepository;
    @Inject
    private EvaluationLogRepository evaluationLogRepository;
    @Inject
    private ClassifierOptionsRequestRepository classifierOptionsRequestRepository;
    @Inject
    private EvaluationOptimizerService evaluationOptimizerService;

    private InstancesRequest instancesRequest;

    private String decisionTreeOptions;
    private String j48Options;
    private String dataMd5Hash;

    @Override
    public void init() throws Exception {
        instancesRequest = new InstancesRequest();
        instancesRequest.setData(TestHelperUtils.loadInstances());
        String instancesJson = toJson(instancesRequest.getData());
        dataMd5Hash = DigestUtils.md5DigestAsHex(instancesJson.getBytes(StandardCharsets.UTF_8));
        DecisionTreeOptions treeOptions = TestHelperUtils.createDecisionTreeOptions();
        treeOptions.setDecisionTreeType(DecisionTreeType.CART);
        decisionTreeOptions = objectMapper.writeValueAsString(treeOptions);
        j48Options = objectMapper.writeValueAsString(TestHelperUtils.createJ48Options());
    }

    @Override
    public void deleteAll() {
        classifierOptionsRequestRepository.deleteAll();
        ersRequestRepository.deleteAll();
        evaluationLogRepository.deleteAll();
    }

    @Test
    void testServiceUnavailable() {
        FeignException.ServiceUnavailable serviceUnavailable = mock(FeignException.ServiceUnavailable.class);
        internalTestErrorStatus(serviceUnavailable, ErsResponseStatus.SERVICE_UNAVAILABLE,
                ErrorCode.SERVICE_UNAVAILABLE);
    }

    @Test
    void testErrorStatus() {
        FeignException.BadRequest badRequest = mock(FeignException.BadRequest.class);
        internalTestErrorStatus(badRequest, ErsResponseStatus.ERROR, ErrorCode.INTERNAL_SERVER_ERROR);
    }

    @Test
    void testsInvalidClassifierOptions() {
        ClassifierOptionsResponse response = TestHelperUtils.createClassifierOptionsResponse(Collections
                .singletonList(TestHelperUtils.createClassifierReport(StringUtils.EMPTY)));
        when(ersClient.getClassifierOptions(any(ClassifierOptionsRequest.class))).thenReturn(response);
        EvaluationResponse evaluationResponse = evaluationOptimizerService.evaluateWithOptimalClassifierOptions(
                instancesRequest);
        assertThat(evaluationResponse).isNotNull();
        assertThat(evaluationResponse.getStatus()).isEqualTo(TechnicalStatus.ERROR);
        List<ClassifierOptionsRequestModel> optionsRequests = classifierOptionsRequestModelRepository.findAll();
        AssertionUtils.hasOneElement(optionsRequests);
        ClassifierOptionsRequestModel requestModel = optionsRequests.get(0);
        assertThat(evaluationResponse.getRequestId()).isNotNull();
        assertThat(requestModel.getResponseStatus()).isEqualTo(ErsResponseStatus.ERROR);
        assertErsSource();
    }

    @Test
    void testEvaluationWithNoClassifierOptionsRequests() {
        ClassifierOptionsResponse response = TestHelperUtils.createClassifierOptionsResponse(Collections
                .singletonList(TestHelperUtils.createClassifierReport(decisionTreeOptions)));
        when(ersClient.getClassifierOptions(any(ClassifierOptionsRequest.class))).thenReturn(response);
        EvaluationResponse evaluationResponse = evaluationOptimizerService.evaluateWithOptimalClassifierOptions(
                instancesRequest);
        assertSuccessEvaluationResponse(evaluationResponse);
        List<ClassifierOptionsRequestModel> optionsRequests = classifierOptionsRequestModelRepository.findAll();
        AssertionUtils.hasOneElement(optionsRequests);
        assertSuccessClassifierOptionsRequestModel(optionsRequests.get(0));
        assertErsSource();
    }

    @Test
    void testDisabledCache() {
        ErsConfig ersConfig = new ErsConfig();
        ersConfig.setUseClassifierOptionsCache(false);
        ReflectionTestUtils.setField(evaluationOptimizerService, "ersConfig", ersConfig);
        evaluationOptimizerService.evaluateWithOptimalClassifierOptions(instancesRequest);
        evaluationOptimizerService.evaluateWithOptimalClassifierOptions(instancesRequest);
        assertThat(classifierOptionsRequestModelRepository.count()).isEqualTo(2L);
        assertThat(classifierOptionsRequestRepository.count()).isEqualTo(2L);
    }

    /**
     * Tests for checking exceeded cache cases:
     * Case 1: It's already been N days after last request
     * Case 2: Last response status is ERROR
     * Case 3: Requests with specified data MD5 hash doesn't exists
     * Case 4: Last response status is SUCCESS, but doesn't contains any classifier options response model
     * Case 5: Last response status is SUCCESS, but classifier options response model has invalid options string
     */
    @Test
    void testExceededClassifierOptionsCache() {
        //Case 1
        ClassifierOptionsRequestModel requestModel = TestHelperUtils.createClassifierOptionsRequestModel(dataMd5Hash,
                LocalDateTime.now().minusDays(ersConfig.getClassifierOptionsCacheDurationInDays() + 1),
                ErsResponseStatus.SUCCESS, Collections.emptyList());
        ClassifierOptionsRequestEntity requestEntity = TestHelperUtils.createClassifierOptionsRequestEntity
                (requestModel.getRequestDate(), requestModel);
        classifierOptionsRequestModelRepository.save(requestModel);
        classifierOptionsRequestRepository.save(requestEntity);
        ClassifierOptionsResponse response = TestHelperUtils.createClassifierOptionsResponse(Collections
                .singletonList(TestHelperUtils.createClassifierReport(decisionTreeOptions)));
        when(ersClient.getClassifierOptions(any(ClassifierOptionsRequest.class))).thenReturn(response);
        EvaluationResponse evaluationResponse = evaluationOptimizerService.evaluateWithOptimalClassifierOptions(
                instancesRequest);
        assertSuccessEvaluationResponse(evaluationResponse);
        List<ClassifierOptionsRequestModel> optionsRequests = classifierOptionsRequestModelRepository.findAll();
        assertThat(optionsRequests.size()).isEqualTo(2);
        assertSuccessClassifierOptionsRequestModel(optionsRequests.get(1));
        List<ClassifierOptionsRequestEntity> requestEntities = classifierOptionsRequestRepository.findAll();
        assertThat(requestEntities.size()).isEqualTo(2);
        assertThat(requestEntities.get(1).getSource()).isEqualTo(ClassifierOptionsRequestSource.ERS);
        deleteAll();
        //Case 2
        requestModel = TestHelperUtils.createClassifierOptionsRequestModel(dataMd5Hash,
                LocalDateTime.now(), ErsResponseStatus.ERROR, Collections.emptyList());
        requestEntity = TestHelperUtils.createClassifierOptionsRequestEntity
                (requestModel.getRequestDate(), requestModel);
        classifierOptionsRequestModelRepository.save(requestModel);
        classifierOptionsRequestRepository.save(requestEntity);
        evaluationResponse = evaluationOptimizerService.evaluateWithOptimalClassifierOptions(instancesRequest);
        assertSuccessEvaluationResponse(evaluationResponse);
        optionsRequests = classifierOptionsRequestModelRepository.findAll();
        assertThat(optionsRequests.size()).isEqualTo(2);
        assertSuccessClassifierOptionsRequestModel(optionsRequests.get(1));
        requestEntities = classifierOptionsRequestRepository.findAll();
        assertThat(requestEntities.size()).isEqualTo(2);
        assertThat(requestEntities.get(1).getSource()).isEqualTo(ClassifierOptionsRequestSource.ERS);
        deleteAll();
        //Case 3
        requestModel = TestHelperUtils.createClassifierOptionsRequestModel(StringUtils.EMPTY, LocalDateTime.now(),
                ErsResponseStatus.SUCCESS, Collections.emptyList());
        requestEntity = TestHelperUtils.createClassifierOptionsRequestEntity
                (requestModel.getRequestDate(), requestModel);
        classifierOptionsRequestModelRepository.save(requestModel);
        classifierOptionsRequestRepository.save(requestEntity);
        evaluationResponse = evaluationOptimizerService.evaluateWithOptimalClassifierOptions(instancesRequest);
        assertSuccessEvaluationResponse(evaluationResponse);
        optionsRequests = classifierOptionsRequestModelRepository.findAll();
        assertThat(optionsRequests.size()).isEqualTo(2);
        assertSuccessClassifierOptionsRequestModel(optionsRequests.get(1));
        requestEntities = classifierOptionsRequestRepository.findAll();
        assertThat(requestEntities.size()).isEqualTo(2);
        assertThat(requestEntities.get(1).getSource()).isEqualTo(ClassifierOptionsRequestSource.ERS);
        deleteAll();
        //Case 4
        requestModel = TestHelperUtils.createClassifierOptionsRequestModel(dataMd5Hash, LocalDateTime.now(),
                ErsResponseStatus.SUCCESS, Collections.emptyList());
        requestEntity = TestHelperUtils.createClassifierOptionsRequestEntity
                (requestModel.getRequestDate(), requestModel);
        classifierOptionsRequestModelRepository.save(requestModel);
        classifierOptionsRequestRepository.save(requestEntity);
        evaluationResponse = evaluationOptimizerService.evaluateWithOptimalClassifierOptions(instancesRequest);
        assertSuccessEvaluationResponse(evaluationResponse);
        optionsRequests = classifierOptionsRequestModelRepository.findAll();
        assertThat(optionsRequests.size()).isEqualTo(2);
        assertSuccessClassifierOptionsRequestModel(optionsRequests.get(1));
        requestEntities = classifierOptionsRequestRepository.findAll();
        assertThat(requestEntities.size()).isEqualTo(2);
        assertThat(requestEntities.get(1).getSource()).isEqualTo(ClassifierOptionsRequestSource.ERS);
        deleteAll();
        //Case 5
        requestModel = TestHelperUtils.createClassifierOptionsRequestModel(dataMd5Hash, LocalDateTime.now(),
                ErsResponseStatus.SUCCESS,
                Collections.singletonList(TestHelperUtils.createClassifierOptionsResponseModel("OPTIONS")));
        requestEntity = TestHelperUtils.createClassifierOptionsRequestEntity
                (requestModel.getRequestDate(), requestModel);
        classifierOptionsRequestModelRepository.save(requestModel);
        classifierOptionsRequestRepository.save(requestEntity);
        evaluationResponse = evaluationOptimizerService.evaluateWithOptimalClassifierOptions(instancesRequest);
        assertSuccessEvaluationResponse(evaluationResponse);
        optionsRequests = classifierOptionsRequestModelRepository.findAll();
        assertThat(optionsRequests.size()).isEqualTo(2);
        assertSuccessClassifierOptionsRequestModel(optionsRequests.get(1));
        requestEntities = classifierOptionsRequestRepository.findAll();
        assertThat(requestEntities.size()).isEqualTo(2);
        assertThat(requestEntities.get(1).getSource()).isEqualTo(ClassifierOptionsRequestSource.ERS);
    }

    @Test
    void testClassifierOptionsCache() {
        ClassifierOptionsRequestModel requestModel =
                TestHelperUtils.createClassifierOptionsRequestModel(dataMd5Hash, LocalDateTime.now().minusDays(1),
                        ErsResponseStatus.SUCCESS, Collections.singletonList(
                                TestHelperUtils.createClassifierOptionsResponseModel(decisionTreeOptions)));
        ClassifierOptionsRequestModel requestModel1 =
                TestHelperUtils.createClassifierOptionsRequestModel(dataMd5Hash, LocalDateTime.now(),
                        ErsResponseStatus.SUCCESS,
                        Collections.singletonList(TestHelperUtils.createClassifierOptionsResponseModel(j48Options)));
        classifierOptionsRequestModelRepository.save(requestModel);
        classifierOptionsRequestModelRepository.save(requestModel1);
        ClassifierOptionsRequestEntity requestEntity =
                TestHelperUtils.createClassifierOptionsRequestEntity(LocalDateTime
                        .now().minusDays(1), requestModel);
        ClassifierOptionsRequestEntity requestEntity1 =
                TestHelperUtils.createClassifierOptionsRequestEntity(LocalDateTime.now(), requestModel1);
        classifierOptionsRequestRepository.save(requestEntity);
        classifierOptionsRequestRepository.save(requestEntity1);
        EvaluationResponse evaluationResponse = evaluationOptimizerService.evaluateWithOptimalClassifierOptions(
                instancesRequest);
        assertSuccessEvaluationResponse(evaluationResponse);
        EvaluationResults results = evaluationResponse.getEvaluationResults();
        assertThat(results.getClassifier()).isInstanceOf(J48.class);
        List<ClassifierOptionsRequestModel> optionsRequests = classifierOptionsRequestModelRepository.findAll();
        List<ClassifierOptionsRequestEntity> requestEntities = classifierOptionsRequestRepository.findAll();
        assertThat(optionsRequests.size()).isEqualTo(2);
        assertThat(requestEntities.size()).isEqualTo(3);
        assertThat(requestEntities.get(2).getSource()).isEqualTo(ClassifierOptionsRequestSource.CACHE);
    }

    /**
     * Tests all classifiers evaluation.
     * Case 1: Decision tree CART
     * Case 2: Logistic regression
     * Case 3: KNN classifier
     * Case 4: Neural network
     * Case 5: Random forests
     * Case 6: J48
     * Case 7: Extra trees
     * Case 8: Stacking
     * Case 9: AdaBoost
     * Case 10: Heterogeneous ensemble
     * Case 11: Modified heterogeneous classifier
     *
     * @throws IOException in case of error
     */
    @Test
    void testClassifiersEvaluation() throws IOException {
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
        performClassifierEvaluationTest(randomForestsOptions, RandomForests.class);
        //Case 6
        performClassifierEvaluationTest(TestHelperUtils.createJ48Options(), J48.class);
        //Case 7
        ExtraTreesOptions extraTreesOptions = TestHelperUtils.createExtraTreesOptions(DecisionTreeType.CART);
        performClassifierEvaluationTest(extraTreesOptions, ExtraTreesClassifier.class);
        //Case 8
        performClassifierEvaluationTest(TestHelperUtils.createStackingOptions(), StackingClassifier.class);
        //Case 9
        performClassifierEvaluationTest(TestHelperUtils.createAdaBoostOptions(), AdaBoostClassifier.class);
        //Case 10
        performClassifierEvaluationTest(TestHelperUtils.createHeterogeneousClassifierOptions(false),
                HeterogeneousClassifier.class);
        //Case 11
        performClassifierEvaluationTest(TestHelperUtils.createHeterogeneousClassifierOptions(true),
                ModifiedHeterogeneousClassifier.class);

    }

    private void internalTestErrorStatus(Exception ex, ErsResponseStatus expectedStatus, ErrorCode expectedErrorCode) {
        when(ersClient.getClassifierOptions(any(ClassifierOptionsRequest.class))).thenThrow(ex);
        EvaluationResponse evaluationResponse = evaluationOptimizerService.evaluateWithOptimalClassifierOptions(
                instancesRequest);
        assertThat(evaluationResponse).isNotNull();
        assertThat(evaluationResponse.getStatus()).isEqualTo(TechnicalStatus.ERROR);
        assertThat(evaluationResponse.getEvaluationResults()).isNull();
        assertThat(evaluationResponse.getErrors()).hasSize(1);
        var error = evaluationResponse.getErrors().iterator().next();
        assertThat(error.getCode()).isEqualTo(expectedErrorCode.name());
        assertThat(error.getMessage()).isEqualTo(expectedErrorCode.getErrorMessage());
        List<ClassifierOptionsRequestModel> optionsRequests = classifierOptionsRequestModelRepository.findAll();
        AssertionUtils.hasOneElement(optionsRequests);
        ClassifierOptionsRequestModel requestModel = optionsRequests.get(0);
        assertThat(evaluationResponse.getRequestId()).isNotNull();
        assertThat(requestModel.getResponseStatus()).isEqualTo(expectedStatus);
        assertThat(requestModel.getClassifierOptionsResponseModels()).isNullOrEmpty();
    }

    private <U extends ClassifierOptions, V extends AbstractClassifier> void performClassifierEvaluationTest(U options,
                                                                                                             Class<V> classifierClazz)
            throws IOException {
        ClassifierOptionsResponse response = TestHelperUtils.createClassifierOptionsResponse(Collections.singletonList(
                TestHelperUtils.createClassifierReport(objectMapper.writeValueAsString(options))));
        when(ersClient.getClassifierOptions(any(ClassifierOptionsRequest.class))).thenReturn(response);
        EvaluationResponse evaluationResponse = evaluationOptimizerService.evaluateWithOptimalClassifierOptions(
                instancesRequest);
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
        assertThat(requestModel.getResponseStatus()).isEqualTo(ErsResponseStatus.SUCCESS);
        assertThat(requestModel.getRequestId()).isNotNull();
        assertThat(requestModel.getNumFolds()).isNotNull();
        assertThat(requestModel.getNumTests()).isNotNull();
        assertThat(requestModel.getSeed()).isNotNull();
        assertThat(requestModel.getEvaluationMethod()).isNotNull();
        assertThat(requestModel.getClassifierOptionsResponseModels()).hasSize(1);
    }

    private void assertErsSource() {
        List<ClassifierOptionsRequestEntity> requestEntities = classifierOptionsRequestRepository.findAll();
        AssertionUtils.hasOneElement(requestEntities);
        assertThat(requestEntities.get(0).getSource()).isEqualTo(ClassifierOptionsRequestSource.ERS);
    }
}
