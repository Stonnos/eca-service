package com.ecaservice.service.evaluation;

import com.ecaservice.AssertionUtils;
import com.ecaservice.TestHelperUtils;
import com.ecaservice.config.CommonConfig;
import com.ecaservice.config.CrossValidationConfig;
import com.ecaservice.config.ers.ErsConfig;
import com.ecaservice.configuation.ClassifierOptionsMapperConfiguration;
import com.ecaservice.configuation.ExecutorConfiguration;
import com.ecaservice.dto.EvaluationResponse;
import com.ecaservice.dto.InstancesRequest;
import com.ecaservice.dto.evaluation.ClassifierOptionsRequest;
import com.ecaservice.dto.evaluation.ClassifierOptionsResponse;
import com.ecaservice.dto.evaluation.ResponseStatus;
import com.ecaservice.mapping.ClassifierInfoMapperImpl;
import com.ecaservice.mapping.ClassifierOptionsRequestMapper;
import com.ecaservice.mapping.ClassifierOptionsRequestMapperImpl;
import com.ecaservice.mapping.ClassifierOptionsRequestModelMapper;
import com.ecaservice.mapping.ClassifierOptionsRequestModelMapperImpl;
import com.ecaservice.mapping.ClassifierOptionsResponseModelMapperImpl;
import com.ecaservice.mapping.ClassifierReportMapper;
import com.ecaservice.mapping.ClassifierReportMapperImpl;
import com.ecaservice.mapping.ErsEvaluationMethodMapperImpl;
import com.ecaservice.mapping.ErsResponseStatusMapper;
import com.ecaservice.mapping.ErsResponseStatusMapperImpl;
import com.ecaservice.mapping.ClassifierInputOptionsMapperImpl;
import com.ecaservice.mapping.EvaluationLogMapperImpl;
import com.ecaservice.mapping.EvaluationRequestMapper;
import com.ecaservice.mapping.EvaluationRequestMapperImpl;
import com.ecaservice.mapping.InstancesConverter;
import com.ecaservice.mapping.InstancesInfoMapperImpl;
import com.ecaservice.model.TechnicalStatus;
import com.ecaservice.model.entity.ClassifierOptionsRequestEntity;
import com.ecaservice.model.entity.ClassifierOptionsRequestModel;
import com.ecaservice.model.entity.ErsResponseStatus;
import com.ecaservice.model.evaluation.ClassifierOptionsRequestSource;
import com.ecaservice.model.options.ActivationFunctionOptions;
import com.ecaservice.model.options.ClassifierOptions;
import com.ecaservice.model.options.DecisionTreeOptions;
import com.ecaservice.model.options.ExtraTreesOptions;
import com.ecaservice.model.options.KNearestNeighboursOptions;
import com.ecaservice.model.options.LogisticOptions;
import com.ecaservice.model.options.NeuralNetworkOptions;
import com.ecaservice.model.options.RandomForestsOptions;
import com.ecaservice.repository.ClassifierOptionsRequestModelRepository;
import com.ecaservice.repository.ClassifierOptionsRequestRepository;
import com.ecaservice.repository.ErsRequestRepository;
import com.ecaservice.repository.EvaluationLogRepository;
import com.ecaservice.service.AbstractJpaTest;
import com.ecaservice.conversion.ClassifierOptionsConverter;
import com.ecaservice.service.ers.ErsRequestService;
import com.ecaservice.service.ers.ErsWebServiceClient;
import com.ecaservice.util.Utils;
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
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.context.annotation.Import;
import org.springframework.util.DigestUtils;
import org.springframework.ws.client.WebServiceIOException;
import weka.classifiers.AbstractClassifier;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link EvaluationOptimizerService} functionality.
 *
 * @author Roman Batygin
 */
@Import({ExecutorConfiguration.class, ClassifierOptionsMapperConfiguration.class,
        CrossValidationConfig.class, EvaluationRequestService.class, InstancesInfoMapperImpl.class,
        ClassifierOptionsRequestModelMapperImpl.class, ClassifierReportMapperImpl.class,
        EvaluationRequestMapperImpl.class, ClassifierOptionsRequestMapperImpl.class,
        ErsConfig.class, ClassifierOptionsConverter.class, EvaluationLogMapperImpl.class,
        EvaluationService.class, ErsEvaluationMethodMapperImpl.class, ErsResponseStatusMapperImpl.class,
        InstancesConverter.class, ClassifierOptionsResponseModelMapperImpl.class,
        ClassifierInputOptionsMapperImpl.class, CommonConfig.class, ClassifierInfoMapperImpl.class})
public class EvaluationOptimizerServiceTest extends AbstractJpaTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final int NUM_THREADS = 2;

    @Inject
    private CrossValidationConfig crossValidationConfig;
    @Inject
    private CommonConfig commonConfig;
    @Inject
    private EvaluationRequestService evaluationRequestService;
    @Mock
    private ErsWebServiceClient ersWebServiceClient;
    @Inject
    private ClassifierOptionsRequestModelMapper classifierOptionsRequestModelMapper;
    @Inject
    private ClassifierReportMapper classifierReportMapper;
    @Inject
    private EvaluationRequestMapper evaluationRequestMapper;
    @Inject
    private ClassifierOptionsRequestMapper classifierOptionsRequestMapper;
    @Inject
    private ErsResponseStatusMapper ersResponseStatusMapper;
    @Inject
    private ErsConfig ersConfig;
    @Inject
    private ClassifierOptionsConverter classifierOptionsConverter;
    @Inject
    private ClassifierOptionsRequestModelRepository classifierOptionsRequestModelRepository;
    @Inject
    private ErsRequestRepository ersRequestRepository;
    @Inject
    private EvaluationLogRepository evaluationLogRepository;
    @Inject
    private ClassifierOptionsRequestRepository classifierOptionsRequestRepository;

    private EvaluationOptimizerService evaluationOptimizerService;

    private InstancesRequest instancesRequest;

    private String decisionTreeOptions;
    private String j48Options;
    private String dataMd5Hash;

    @Override
    public void init() throws Exception {
        instancesRequest = new InstancesRequest();
        instancesRequest.setData(TestHelperUtils.loadInstances());
        ErsRequestService ersRequestService = new ErsRequestService(ersWebServiceClient, ersRequestRepository,
                classifierOptionsRequestModelRepository, classifierReportMapper, ersResponseStatusMapper, ersConfig);
        evaluationOptimizerService = new EvaluationOptimizerService(crossValidationConfig, commonConfig,
                evaluationRequestService,
                classifierOptionsRequestModelMapper, ersRequestService, evaluationRequestMapper,
                classifierOptionsRequestMapper, classifierOptionsConverter, classifierOptionsRequestRepository);
        dataMd5Hash = DigestUtils.md5DigestAsHex(
                Utils.toXmlInstances(instancesRequest.getData()).getBytes(StandardCharsets.UTF_8));
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
    public void testServiceUnavailable() {
        when(ersWebServiceClient.getClassifierOptions(any(ClassifierOptionsRequest.class))).thenThrow(
                new WebServiceIOException("error"));
        EvaluationResponse evaluationResponse = evaluationOptimizerService.evaluateWithOptimalClassifierOptions(
                instancesRequest);
        assertThat(evaluationResponse).isNotNull();
        assertThat(evaluationResponse.getStatus()).isEqualTo(TechnicalStatus.ERROR);
        assertThat(evaluationResponse.getEvaluationResults()).isNull();
        List<ClassifierOptionsRequestModel> optionsRequests = classifierOptionsRequestModelRepository.findAll();
        AssertionUtils.assertSingletonList(optionsRequests);
        ClassifierOptionsRequestModel requestModel = optionsRequests.get(0);
        assertThat(evaluationResponse.getRequestId()).isNotNull();
        assertThat(requestModel.getResponseStatus()).isEqualTo(ErsResponseStatus.ERROR);
        assertThat(requestModel.getClassifierOptionsResponseModels()).isNullOrEmpty();
    }

    @Test
    public void testsInvalidClassifierOptions() {
        ClassifierOptionsResponse response = TestHelperUtils.createClassifierOptionsResponse(Collections
                .singletonList(TestHelperUtils.createClassifierReport(StringUtils.EMPTY)), ResponseStatus.SUCCESS);
        when(ersWebServiceClient.getClassifierOptions(any(ClassifierOptionsRequest.class))).thenReturn(response);
        EvaluationResponse evaluationResponse = evaluationOptimizerService.evaluateWithOptimalClassifierOptions(
                instancesRequest);
        assertThat(evaluationResponse).isNotNull();
        assertThat(evaluationResponse.getStatus()).isEqualTo(TechnicalStatus.ERROR);
        List<ClassifierOptionsRequestModel> optionsRequests = classifierOptionsRequestModelRepository.findAll();
        AssertionUtils.assertSingletonList(optionsRequests);
        ClassifierOptionsRequestModel requestModel = optionsRequests.get(0);
        assertThat(evaluationResponse.getRequestId()).isNotNull();
        assertThat(requestModel.getResponseStatus()).isEqualTo(ErsResponseStatus.ERROR);
        assertErsSource();
    }

    @Test
    public void testEvaluationWithNoClassifierOptionsRequests() {
        ClassifierOptionsResponse response = TestHelperUtils.createClassifierOptionsResponse(Collections
                .singletonList(TestHelperUtils.createClassifierReport(decisionTreeOptions)), ResponseStatus.SUCCESS);
        when(ersWebServiceClient.getClassifierOptions(any(ClassifierOptionsRequest.class))).thenReturn(response);
        EvaluationResponse evaluationResponse = evaluationOptimizerService.evaluateWithOptimalClassifierOptions(
                instancesRequest);
        assertSuccessEvaluationResponse(evaluationResponse);
        List<ClassifierOptionsRequestModel> optionsRequests = classifierOptionsRequestModelRepository.findAll();
        AssertionUtils.assertSingletonList(optionsRequests);
        assertSuccessClassifierOptionsRequestModel(optionsRequests.get(0));
        assertErsSource();
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
    public void testExceededClassifierOptionsCache() {
        //Case 1
        ClassifierOptionsRequestModel requestModel = TestHelperUtils.createClassifierOptionsRequestModel(dataMd5Hash,
                LocalDateTime.now().minusDays(commonConfig.getClassifierOptionsCacheDurationInDays() + 1),
                ErsResponseStatus.SUCCESS, Collections.emptyList());
        ClassifierOptionsRequestEntity requestEntity = TestHelperUtils.createClassifierOptionsRequestEntity
                (requestModel.getRequestDate(), requestModel);
        classifierOptionsRequestModelRepository.save(requestModel);
        classifierOptionsRequestRepository.save(requestEntity);
        ClassifierOptionsResponse response = TestHelperUtils.createClassifierOptionsResponse(Collections
                .singletonList(TestHelperUtils.createClassifierReport(decisionTreeOptions)), ResponseStatus.SUCCESS);
        when(ersWebServiceClient.getClassifierOptions(any(ClassifierOptionsRequest.class))).thenReturn(response);
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
    public void testClassifierOptionsCache() {
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

    @Test
    public void testClassifierOptionsCacheInMultiThreadEnvironment() throws Exception {
        ClassifierOptionsResponse response = TestHelperUtils.createClassifierOptionsResponse(Collections
                .singletonList(TestHelperUtils.createClassifierReport(decisionTreeOptions)), ResponseStatus.SUCCESS);
        when(ersWebServiceClient.getClassifierOptions(any(ClassifierOptionsRequest.class))).thenReturn(response);
        final CountDownLatch finishedLatch = new CountDownLatch(NUM_THREADS);
        ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);
        for (int i = 0; i < NUM_THREADS; i++) {
            executorService.submit(() -> {
                evaluationOptimizerService.evaluateWithOptimalClassifierOptions(
                        instancesRequest);
                finishedLatch.countDown();
            });
        }
        finishedLatch.await();
        executorService.shutdownNow();
        List<ClassifierOptionsRequestEntity> requestEntities = classifierOptionsRequestRepository.findAll();
        assertThat(requestEntities.size()).isEqualTo(2);
        assertThat(classifierOptionsRequestModelRepository.count()).isOne();
        assertThat(requestEntities.get(0).getSource()).isEqualTo(ClassifierOptionsRequestSource.ERS);
        assertThat(requestEntities.get(1).getSource()).isEqualTo(ClassifierOptionsRequestSource.CACHE);
        assertThat(requestEntities.get(0).getClassifierOptionsRequestModel().getId()).isEqualTo(
                requestEntities.get(1).getClassifierOptionsRequestModel().getId());
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

    private <U extends ClassifierOptions, V extends AbstractClassifier> void performClassifierEvaluationTest(U options,
                                                                                                             Class<V> classifierClazz)
            throws IOException {
        ClassifierOptionsResponse response = TestHelperUtils.createClassifierOptionsResponse(Collections.singletonList(
                TestHelperUtils.createClassifierReport(objectMapper.writeValueAsString(options))),
                ResponseStatus.SUCCESS);
        when(ersWebServiceClient.getClassifierOptions(any(ClassifierOptionsRequest.class))).thenReturn(response);
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
        assertThat(requestModel.getClassifierOptionsResponseModels()).isNotNull();
        assertThat(requestModel.getClassifierOptionsResponseModels().size()).isOne();
    }

    private void assertErsSource() {
        List<ClassifierOptionsRequestEntity> requestEntities = classifierOptionsRequestRepository.findAll();
        AssertionUtils.assertSingletonList(requestEntities);
        assertThat(requestEntities.get(0).getSource()).isEqualTo(ClassifierOptionsRequestSource.ERS);
    }
}
