package com.ecaservice.server.service.evaluation;

import com.ecaservice.base.model.ErrorCode;
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
import com.ecaservice.s3.client.minio.model.GetPresignedUrlObject;
import com.ecaservice.s3.client.minio.service.ObjectStorageService;
import com.ecaservice.server.AssertionUtils;
import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.config.AppProperties;
import com.ecaservice.server.config.ClassifiersProperties;
import com.ecaservice.server.config.CrossValidationConfig;
import com.ecaservice.server.config.ers.ErsConfig;
import com.ecaservice.server.configuation.ExecutorConfiguration;
import com.ecaservice.server.mapping.ClassifierInfoMapperImpl;
import com.ecaservice.server.mapping.ClassifierOptionsRequestMapperImpl;
import com.ecaservice.server.mapping.ClassifierOptionsRequestModelMapperImpl;
import com.ecaservice.server.mapping.ClassifierReportMapperImpl;
import com.ecaservice.server.mapping.DateTimeConverter;
import com.ecaservice.server.mapping.ErsResponseStatusMapperImpl;
import com.ecaservice.server.mapping.EvaluationLogMapperImpl;
import com.ecaservice.server.mapping.EvaluationRequestMapperImpl;
import com.ecaservice.server.mapping.InstancesInfoMapperImpl;
import com.ecaservice.server.model.entity.ClassifierOptionsRequestEntity;
import com.ecaservice.server.model.entity.ClassifierOptionsRequestModel;
import com.ecaservice.server.model.entity.ErsResponseStatus;
import com.ecaservice.server.model.entity.InstancesInfo;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.model.evaluation.ClassifierOptionsRequestSource;
import com.ecaservice.server.model.evaluation.EvaluationResultsDataModel;
import com.ecaservice.server.model.evaluation.InstancesRequestDataModel;
import com.ecaservice.server.repository.ClassifierOptionsRequestModelRepository;
import com.ecaservice.server.repository.ClassifierOptionsRequestRepository;
import com.ecaservice.server.repository.ErsRequestRepository;
import com.ecaservice.server.repository.EvaluationLogRepository;
import com.ecaservice.server.repository.InstancesInfoRepository;
import com.ecaservice.server.service.AbstractJpaTest;
import com.ecaservice.server.service.InstancesInfoService;
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
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

import javax.inject.Inject;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.ecaservice.server.TestHelperUtils.createInstancesInfo;
import static com.ecaservice.server.util.InstancesUtils.md5Hash;
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
        ErsConfig.class, EvaluationLogMapperImpl.class, ClassifiersProperties.class,
        EvaluationService.class, ErsResponseStatusMapperImpl.class,
        InstancesInfoMapperImpl.class, ErsRequestService.class, InstancesInfoService.class,
        EvaluationOptimizerService.class, ClassifierInfoMapperImpl.class, ErsErrorHandler.class,
        ClassifierOptionsCacheService.class, DateTimeConverter.class})
class EvaluationOptimizerServiceTest extends AbstractJpaTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String MODEL_DOWNLOAD_URL = "http//:localhost/model";

    @MockBean
    private ErsClient ersClient;
    @MockBean
    private ErsRequestSender ersRequestSender;
    @MockBean
    private EvaluationResultsService evaluationResultsService;
    @MockBean
    private ClassifierInitializerService classifierInitializerService;
    @MockBean
    private ObjectStorageService objectStorageService;
    @Inject
    private ErsConfig ersConfig;
    @Inject
    private ClassifierOptionsRequestModelRepository classifierOptionsRequestModelRepository;
    @Inject
    private ErsRequestRepository ersRequestRepository;
    @Inject
    private EvaluationLogRepository evaluationLogRepository;
    @Inject
    private InstancesInfoRepository instancesInfoRepository;
    @Inject
    private ClassifierOptionsRequestRepository classifierOptionsRequestRepository;
    @Inject
    private EvaluationOptimizerService evaluationOptimizerService;

    private InstancesRequestDataModel instancesRequestDataModel;

    private String decisionTreeOptions;
    private String j48Options;

    private InstancesInfo instancesInfo;

    @Override
    public void init() throws Exception {
        initInstancesData();
        DecisionTreeOptions treeOptions = TestHelperUtils.createDecisionTreeOptions();
        treeOptions.setDecisionTreeType(DecisionTreeType.CART);
        decisionTreeOptions = objectMapper.writeValueAsString(treeOptions);
        j48Options = objectMapper.writeValueAsString(TestHelperUtils.createJ48Options());
        when(objectStorageService.getObjectPresignedProxyUrl(any(GetPresignedUrlObject.class)))
                .thenReturn(MODEL_DOWNLOAD_URL);
    }

    @Override
    public void deleteAll() {
        classifierOptionsRequestRepository.deleteAll();
        ersRequestRepository.deleteAll();
        evaluationLogRepository.deleteAll();
        instancesInfoRepository.deleteAll();
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
        EvaluationResultsDataModel evaluationResultsDataModel =
                evaluationOptimizerService.evaluateWithOptimalClassifierOptions(instancesRequestDataModel);
        assertThat(evaluationResultsDataModel).isNotNull();
        assertThat(evaluationResultsDataModel.getStatus()).isEqualTo(RequestStatus.ERROR);
        List<ClassifierOptionsRequestModel> optionsRequests = classifierOptionsRequestModelRepository.findAll();
        AssertionUtils.hasOneElement(optionsRequests);
        ClassifierOptionsRequestModel requestModel = optionsRequests.iterator().next();
        assertThat(evaluationResultsDataModel.getRequestId()).isNotNull();
        assertThat(requestModel.getResponseStatus()).isEqualTo(ErsResponseStatus.ERROR);
        assertErsSource();
    }

    @Test
    void testEvaluationWithNoClassifierOptionsRequests() {
        ClassifierOptionsResponse response = TestHelperUtils.createClassifierOptionsResponse(Collections
                .singletonList(TestHelperUtils.createClassifierReport(decisionTreeOptions)));
        when(ersClient.getClassifierOptions(any(ClassifierOptionsRequest.class))).thenReturn(response);
        EvaluationResultsDataModel evaluationResultsDataModel =
                evaluationOptimizerService.evaluateWithOptimalClassifierOptions(instancesRequestDataModel);
        assertSuccessEvaluationResponse(evaluationResultsDataModel);
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
        evaluationOptimizerService.evaluateWithOptimalClassifierOptions(instancesRequestDataModel);
        instancesRequestDataModel.setRequestId(UUID.randomUUID().toString());
        evaluationOptimizerService.evaluateWithOptimalClassifierOptions(instancesRequestDataModel);
        assertThat(classifierOptionsRequestModelRepository.count()).isEqualTo(2L);
        assertThat(classifierOptionsRequestRepository.count()).isEqualTo(2L);
    }

    @Test
    void testExceededClassifierOptionsCacheWithLastResponseIsAfterNDays() {
        ClassifierOptionsRequestModel requestModel = TestHelperUtils.createClassifierOptionsRequestModel(instancesInfo,
                LocalDateTime.now().minusDays(ersConfig.getClassifierOptionsCacheDurationInDays() + 1),
                ErsResponseStatus.SUCCESS, Collections.emptyList());
        ClassifierOptionsRequestEntity requestEntity = TestHelperUtils.createClassifierOptionsRequestEntity
                (requestModel.getRequestDate(), requestModel);
        classifierOptionsRequestModelRepository.save(requestModel);
        classifierOptionsRequestRepository.save(requestEntity);
        ClassifierOptionsResponse response = TestHelperUtils.createClassifierOptionsResponse(Collections
                .singletonList(TestHelperUtils.createClassifierReport(decisionTreeOptions)));
        EvaluationResultsDataModel evaluationResultsDataModel = evaluate();
        assertSuccessEvaluationResponse(evaluationResultsDataModel);
        List<ClassifierOptionsRequestModel> optionsRequests = classifierOptionsRequestModelRepository.findAll();
        assertThat(optionsRequests).hasSize(2);
        assertSuccessClassifierOptionsRequestModel(optionsRequests.get(1));
        List<ClassifierOptionsRequestEntity> requestEntities = classifierOptionsRequestRepository.findAll();
        assertThat(requestEntities).hasSize(2);
        assertThat(requestEntities.get(1).getSource()).isEqualTo(ClassifierOptionsRequestSource.ERS);
    }

    @Test
    void testExceededClassifierOptionsCacheWithLastHasErrorStatus() {
        var requestModel = TestHelperUtils.createClassifierOptionsRequestModel(instancesInfo,
                LocalDateTime.now(), ErsResponseStatus.ERROR, Collections.emptyList());
        var requestEntity = TestHelperUtils.createClassifierOptionsRequestEntity
                (requestModel.getRequestDate(), requestModel);
        classifierOptionsRequestModelRepository.save(requestModel);
        classifierOptionsRequestRepository.save(requestEntity);
        EvaluationResultsDataModel evaluationResultsDataModel = evaluate();
        assertSuccessEvaluationResponse(evaluationResultsDataModel);
        var optionsRequests = classifierOptionsRequestModelRepository.findAll();
        assertThat(optionsRequests).hasSize(2);
        assertSuccessClassifierOptionsRequestModel(optionsRequests.get(1));
        var requestEntities = classifierOptionsRequestRepository.findAll();
        assertThat(requestEntities).hasSize(2);
        assertThat(requestEntities.get(1).getSource()).isEqualTo(ClassifierOptionsRequestSource.ERS);
    }

    @Test
    void testExceededClassifierOptionsCacheWithDataMd5HashDoesntExists() {
        var anotherInstancesInfo = createInstancesInfo();
        anotherInstancesInfo.setDataMd5Hash("anotherHash");
        instancesInfoRepository.save(anotherInstancesInfo);
        var requestModel = TestHelperUtils.createClassifierOptionsRequestModel(anotherInstancesInfo,
                LocalDateTime.now(),
                ErsResponseStatus.SUCCESS, Collections.emptyList());
        var requestEntity = TestHelperUtils.createClassifierOptionsRequestEntity
                (requestModel.getRequestDate(), requestModel);
        classifierOptionsRequestModelRepository.save(requestModel);
        classifierOptionsRequestRepository.save(requestEntity);
        EvaluationResultsDataModel evaluationResultsDataModel = evaluate();
        assertSuccessEvaluationResponse(evaluationResultsDataModel);
        var optionsRequests = classifierOptionsRequestModelRepository.findAll();
        assertThat(optionsRequests).hasSize(2);
        assertSuccessClassifierOptionsRequestModel(optionsRequests.get(1));
        var requestEntities = classifierOptionsRequestRepository.findAll();
        assertThat(requestEntities).hasSize(2);
        assertThat(requestEntities.get(1).getSource()).isEqualTo(ClassifierOptionsRequestSource.ERS);
    }

    @Test
    void testExceededClassifierOptionsCacheWithLastResponseIsSuccessButDoesntContainsAnyClassifierOptionsResponseModel() {
        var requestModel = TestHelperUtils.createClassifierOptionsRequestModel(instancesInfo, LocalDateTime.now(),
                ErsResponseStatus.SUCCESS, Collections.emptyList());
        var requestEntity = TestHelperUtils.createClassifierOptionsRequestEntity
                (requestModel.getRequestDate(), requestModel);
        classifierOptionsRequestModelRepository.save(requestModel);
        classifierOptionsRequestRepository.save(requestEntity);
        EvaluationResultsDataModel evaluationResultsDataModel = evaluate();
        assertSuccessEvaluationResponse(evaluationResultsDataModel);
        var optionsRequests = classifierOptionsRequestModelRepository.findAll();
        assertThat(optionsRequests).hasSize(2);
        assertSuccessClassifierOptionsRequestModel(optionsRequests.get(1));
        var requestEntities = classifierOptionsRequestRepository.findAll();
        assertThat(requestEntities).hasSize(2);
        assertThat(requestEntities.get(1).getSource()).isEqualTo(ClassifierOptionsRequestSource.ERS);
    }

    @Test
    void testExceededClassifierOptionsCacheWithLastResponseIsSuccessButHasInvalidClassifierOptionsStringInResponse() {
        var requestModel = TestHelperUtils.createClassifierOptionsRequestModel(instancesInfo, LocalDateTime.now(),
                ErsResponseStatus.SUCCESS,
                Collections.singletonList(TestHelperUtils.createClassifierOptionsResponseModel("OPTIONS")));
        var requestEntity = TestHelperUtils.createClassifierOptionsRequestEntity
                (requestModel.getRequestDate(), requestModel);
        classifierOptionsRequestModelRepository.save(requestModel);
        classifierOptionsRequestRepository.save(requestEntity);
        EvaluationResultsDataModel evaluationResultsDataModel = evaluate();
        assertSuccessEvaluationResponse(evaluationResultsDataModel);
        var optionsRequests = classifierOptionsRequestModelRepository.findAll();
        assertThat(optionsRequests).hasSize(2);
        assertSuccessClassifierOptionsRequestModel(optionsRequests.get(1));
        var requestEntities = classifierOptionsRequestRepository.findAll();
        assertThat(requestEntities).hasSize(2);
        assertThat(requestEntities.get(1).getSource()).isEqualTo(ClassifierOptionsRequestSource.ERS);
    }

    @Test
    void testClassifierOptionsCache() {
        ClassifierOptionsRequestModel requestModel =
                TestHelperUtils.createClassifierOptionsRequestModel(instancesInfo, LocalDateTime.now().minusDays(1),
                        ErsResponseStatus.SUCCESS, Collections.singletonList(
                                TestHelperUtils.createClassifierOptionsResponseModel(decisionTreeOptions)));
        ClassifierOptionsRequestModel requestModel1 =
                TestHelperUtils.createClassifierOptionsRequestModel(instancesInfo, LocalDateTime.now(),
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
        var evaluationResultsDataModel = evaluationOptimizerService.evaluateWithOptimalClassifierOptions(
                instancesRequestDataModel);
        assertSuccessEvaluationResponse(evaluationResultsDataModel);
        EvaluationResults results = evaluationResultsDataModel.getEvaluationResults();
        assertThat(results.getClassifier()).isInstanceOf(J48.class);
        List<ClassifierOptionsRequestModel> optionsRequests = classifierOptionsRequestModelRepository.findAll();
        List<ClassifierOptionsRequestEntity> requestEntities = classifierOptionsRequestRepository.findAll();
        assertThat(optionsRequests).hasSize(2);
        assertThat(requestEntities).hasSize(3);
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

    private EvaluationResultsDataModel evaluate() {
        ClassifierOptionsResponse response = TestHelperUtils.createClassifierOptionsResponse(Collections
                .singletonList(TestHelperUtils.createClassifierReport(decisionTreeOptions)));
        when(ersClient.getClassifierOptions(any(ClassifierOptionsRequest.class))).thenReturn(response);
        return evaluationOptimizerService.evaluateWithOptimalClassifierOptions(instancesRequestDataModel);
    }

    private void internalTestErrorStatus(Exception ex, ErsResponseStatus expectedStatus, ErrorCode expectedErrorCode) {
        when(ersClient.getClassifierOptions(any(ClassifierOptionsRequest.class))).thenThrow(ex);
        EvaluationResultsDataModel evaluationResultsDataModel =
                evaluationOptimizerService.evaluateWithOptimalClassifierOptions(instancesRequestDataModel);
        assertThat(evaluationResultsDataModel).isNotNull();
        assertThat(evaluationResultsDataModel.getStatus()).isEqualTo(RequestStatus.ERROR);
        assertThat(evaluationResultsDataModel.getEvaluationResults()).isNull();
        assertThat(evaluationResultsDataModel.getErrorCode()).isEqualTo(expectedErrorCode);
        List<ClassifierOptionsRequestModel> optionsRequests = classifierOptionsRequestModelRepository.findAll();
        AssertionUtils.hasOneElement(optionsRequests);
        ClassifierOptionsRequestModel requestModel = optionsRequests.get(0);
        assertThat(evaluationResultsDataModel.getRequestId()).isNotNull();
        assertThat(requestModel.getResponseStatus()).isEqualTo(expectedStatus);
        assertThat(requestModel.getClassifierOptionsResponseModels()).isNullOrEmpty();
    }

    private <U extends ClassifierOptions, V extends AbstractClassifier> void performClassifierEvaluationTest(U options,
                                                                                                             Class<V> classifierClazz)
            throws IOException {
        ClassifierOptionsResponse response = TestHelperUtils.createClassifierOptionsResponse(Collections.singletonList(
                TestHelperUtils.createClassifierReport(objectMapper.writeValueAsString(options))));
        when(ersClient.getClassifierOptions(any(ClassifierOptionsRequest.class))).thenReturn(response);
        EvaluationResultsDataModel evaluationResultsDataModel =
                evaluationOptimizerService.evaluateWithOptimalClassifierOptions(instancesRequestDataModel);
        assertSuccessEvaluationResponse(evaluationResultsDataModel);
        assertThat(evaluationResultsDataModel.getEvaluationResults().getClassifier()).isInstanceOf(classifierClazz);
        deleteAll();
    }

    private void assertSuccessEvaluationResponse(EvaluationResultsDataModel evaluationResultsDataModel) {
        assertThat(evaluationResultsDataModel).isNotNull();
        assertThat(evaluationResultsDataModel.getRequestId()).isNotNull();
        assertThat(evaluationResultsDataModel.getStatus()).isEqualTo(RequestStatus.FINISHED);
        assertThat(evaluationResultsDataModel.getModelUrl()).isEqualTo(MODEL_DOWNLOAD_URL);
    }

    private void assertSuccessClassifierOptionsRequestModel(ClassifierOptionsRequestModel requestModel) {
        assertThat(requestModel.getInstancesInfo()).isNotNull();
        assertThat(requestModel.getInstancesInfo().getDataMd5Hash()).isNotNull();
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

    private void initInstancesData() {
        Instances data = TestHelperUtils.loadInstances();
        String dataMd5Hash = md5Hash(data);
        instancesInfo = createInstancesInfo();
        instancesInfo.setDataMd5Hash(dataMd5Hash);
        instancesInfoRepository.save(instancesInfo);
        instancesRequestDataModel = new InstancesRequestDataModel(UUID.randomUUID().toString(), dataMd5Hash, data);
    }
}
