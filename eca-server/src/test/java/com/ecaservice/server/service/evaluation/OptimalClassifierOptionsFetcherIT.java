package com.ecaservice.server.service.evaluation;

import com.ecaservice.classifier.options.config.ClassifiersOptionsAutoConfiguration;
import com.ecaservice.classifier.options.model.DecisionTreeOptions;
import com.ecaservice.core.filter.service.FilterTemplateService;
import com.ecaservice.core.lock.aspect.LockExecutionAspect;
import com.ecaservice.core.lock.config.CoreLockAutoConfiguration;
import com.ecaservice.core.lock.metrics.LockMeterService;
import com.ecaservice.ers.dto.ClassifierOptionsRequest;
import com.ecaservice.ers.dto.ClassifierOptionsResponse;
import com.ecaservice.s3.client.minio.service.ObjectStorageService;
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
import com.ecaservice.server.model.data.InstancesMetaDataModel;
import com.ecaservice.server.model.entity.ClassifierOptionsRequestEntity;
import com.ecaservice.server.model.evaluation.ClassifierOptionsRequestSource;
import com.ecaservice.server.model.evaluation.InstancesRequestDataModel;
import com.ecaservice.server.repository.AttributesInfoRepository;
import com.ecaservice.server.repository.ClassifierOptionsRequestModelRepository;
import com.ecaservice.server.repository.ClassifierOptionsRequestRepository;
import com.ecaservice.server.repository.ErsRequestRepository;
import com.ecaservice.server.repository.EvaluationLogRepository;
import com.ecaservice.server.repository.InstancesInfoRepository;
import com.ecaservice.server.service.AbstractJpaTest;
import com.ecaservice.server.service.InstancesInfoService;
import com.ecaservice.server.service.InstancesProvider;
import com.ecaservice.server.service.data.InstancesLoaderService;
import com.ecaservice.server.service.data.InstancesMetaDataService;
import com.ecaservice.server.service.ers.ErsClient;
import com.ecaservice.server.service.ers.ErsErrorHandler;
import com.ecaservice.server.service.ers.ErsRequestSender;
import com.ecaservice.server.service.ers.ErsRequestService;
import com.ecaservice.server.service.evaluation.initializers.ClassifierInitializerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import eca.core.evaluation.EvaluationMethod;
import eca.ensemble.forests.DecisionTreeType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.DockerComposeContainer;
import weka.core.Instances;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.ecaservice.server.TestHelperUtils.NUM_FOLDS;
import static com.ecaservice.server.TestHelperUtils.NUM_TESTS;
import static com.ecaservice.server.TestHelperUtils.SEED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link OptimalClassifierOptionsFetcher} functionality.
 *
 * @author Roman Batygin
 */
@Slf4j
@EnableAspectJAutoProxy
@Import({ExecutorConfiguration.class, ClassifiersOptionsAutoConfiguration.class, AppProperties.class,
        CrossValidationConfig.class, EvaluationRequestService.class, InstancesInfoMapperImpl.class,
        ClassifierOptionsRequestModelMapperImpl.class, ClassifierReportMapperImpl.class,
        EvaluationRequestMapperImpl.class, ClassifierOptionsRequestMapperImpl.class, ClassifiersProperties.class,
        ErsConfig.class, EvaluationLogMapperImpl.class, LockExecutionAspect.class, ErsErrorHandler.class,
        EvaluationService.class, ErsResponseStatusMapperImpl.class, OptimalClassifierOptionsFetcherImpl.class,
        InstancesInfoMapperImpl.class, ErsRequestService.class, InstancesInfoService.class, EvaluationLogService.class,
        ClassifierInfoMapperImpl.class, RedisAutoConfiguration.class, InstancesProvider.class,
        OptimalClassifierOptionsCacheService.class, DateTimeConverter.class, CoreLockAutoConfiguration.class})
@TestPropertySource("classpath:application-it.properties")
class OptimalClassifierOptionsFetcherIT extends AbstractJpaTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final int NUM_THREADS = 2;

    @MockBean
    private ErsClient ersClient;
    @MockBean
    private LockMeterService lockMeterService;
    @MockBean
    private FilterTemplateService filterTemplateService;
    @MockBean
    private ObjectStorageService objectStorageService;
    @MockBean
    private InstancesMetaDataService instancesMetaDataService;
    @MockBean
    private InstancesLoaderService instancesLoaderService;
    @MockBean
    private ClassifierInitializerService classifierInitializerService;
    @MockBean
    private EvaluationResultsService evaluationResultsService;
    @MockBean
    private ErsRequestSender ersRequestSender;
    @Autowired
    private ClassifierOptionsRequestModelRepository classifierOptionsRequestModelRepository;
    @Autowired
    private ErsRequestRepository ersRequestRepository;
    @Autowired
    private EvaluationLogRepository evaluationLogRepository;
    @Autowired
    private InstancesInfoRepository instancesInfoRepository;
    @Autowired
    private AttributesInfoRepository attributesInfoRepository;
    @Autowired
    private ClassifierOptionsRequestRepository classifierOptionsRequestRepository;
    @Autowired
    private OptimalClassifierOptionsFetcher optimalClassifierOptionsFetcher;

    private Instances data;
    private String dataUuid;

    private String decisionTreeOptions;

    private static DockerComposeContainer dockerComposeContainer =
            new DockerComposeContainer(new File("src/test/resources/docker-compose-test.yaml"));

    @BeforeAll
    static void start() {
        dockerComposeContainer.start();
    }

    @AfterAll
    static void stop() {
        dockerComposeContainer.stop();
    }

    @Override
    public void init() throws Exception {
        data = TestHelperUtils.loadInstances();
        dataUuid = UUID.randomUUID().toString();
        DecisionTreeOptions treeOptions = TestHelperUtils.createDecisionTreeOptions();
        treeOptions.setDecisionTreeType(DecisionTreeType.CART);
        decisionTreeOptions = objectMapper.writeValueAsString(treeOptions);
        mockLoadInstances();
    }

    private void mockLoadInstances() {
        var instancesDataModel =
                new InstancesMetaDataModel(dataUuid, data.relationName(), data.numInstances(), data.numAttributes(),
                        data.numClasses(), data.classAttribute().name(), "instances", Collections.emptyList());
        when(instancesMetaDataService.getInstancesMetaData(dataUuid)).thenReturn(instancesDataModel);
        when(instancesLoaderService.loadInstances(dataUuid)).thenReturn(data);
    }

    @Override
    public void deleteAll() {
        classifierOptionsRequestRepository.deleteAll();
        ersRequestRepository.deleteAll();
        evaluationLogRepository.deleteAll();
        attributesInfoRepository.deleteAll();
        instancesInfoRepository.deleteAll();
    }

    @Test
    void testClassifierOptionsCacheInMultiThreadEnvironment() throws Exception {
        ClassifierOptionsResponse response = TestHelperUtils.createClassifierOptionsResponse(Collections
                .singletonList(TestHelperUtils.createClassifierReport(decisionTreeOptions)));
        when(ersClient.getClassifierOptions(any(ClassifierOptionsRequest.class))).thenReturn(response);
        final CountDownLatch finishedLatch = new CountDownLatch(NUM_THREADS);
        ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);
        for (int i = 0; i < NUM_THREADS; i++) {
            executorService.submit(() -> {
                try {
                    var instancesRequestDataModel =
                            new InstancesRequestDataModel(UUID.randomUUID().toString(), dataUuid,
                                    EvaluationMethod.CROSS_VALIDATION, NUM_FOLDS, NUM_TESTS, SEED);
                    optimalClassifierOptionsFetcher.getOptimalClassifierOptions(instancesRequestDataModel);
                } catch (Exception ex) {
                    log.error("Error while get optimal classifier options: {}", ex.getMessage());
                } finally {
                    finishedLatch.countDown();
                }
            });
        }
        finishedLatch.await();
        executorService.shutdownNow();
        List<ClassifierOptionsRequestEntity> requestEntities = classifierOptionsRequestRepository.findAll();
        assertThat(requestEntities).hasSize(2);
        assertThat(classifierOptionsRequestModelRepository.count()).isOne();
        assertThat(requestEntities.get(0).getSource()).isEqualTo(ClassifierOptionsRequestSource.ERS);
        assertThat(requestEntities.get(1).getSource()).isEqualTo(ClassifierOptionsRequestSource.CACHE);
        assertThat(requestEntities.get(0).getClassifierOptionsRequestModel().getId()).isEqualTo(
                requestEntities.get(1).getClassifierOptionsRequestModel().getId());
    }
}
