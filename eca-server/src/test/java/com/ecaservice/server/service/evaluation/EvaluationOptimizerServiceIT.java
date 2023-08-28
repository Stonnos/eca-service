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
import com.ecaservice.server.model.entity.ClassifierOptionsRequestEntity;
import com.ecaservice.server.model.evaluation.ClassifierOptionsRequestSource;
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
import eca.ensemble.forests.DecisionTreeType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.DockerComposeContainer;
import weka.core.Instances;

import javax.inject.Inject;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.ecaservice.server.util.InstancesUtils.md5Hash;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link EvaluationOptimizerService} functionality.
 *
 * @author Roman Batygin
 */
@EnableAspectJAutoProxy
@Import({ExecutorConfiguration.class, ClassifiersOptionsAutoConfiguration.class, AppProperties.class,
        CrossValidationConfig.class, EvaluationRequestService.class, InstancesInfoMapperImpl.class,
        ClassifierOptionsRequestModelMapperImpl.class, ClassifierReportMapperImpl.class,
        EvaluationRequestMapperImpl.class, ClassifierOptionsRequestMapperImpl.class, ClassifiersProperties.class,
        ErsConfig.class, EvaluationLogMapperImpl.class, LockExecutionAspect.class, ErsErrorHandler.class,
        EvaluationService.class, ErsResponseStatusMapperImpl.class, OptimalClassifierOptionsFetcherImpl.class,
        InstancesInfoMapperImpl.class, ErsRequestService.class, InstancesInfoService.class,
        EvaluationOptimizerService.class, ClassifierInfoMapperImpl.class, RedisAutoConfiguration.class,
        OptimalClassifierOptionsCacheService.class, DateTimeConverter.class, CoreLockAutoConfiguration.class})
@TestPropertySource("classpath:application-it.properties")
class EvaluationOptimizerServiceIT extends AbstractJpaTest {

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
    private ClassifierInitializerService classifierInitializerService;
    @MockBean
    private EvaluationResultsService evaluationResultsService;
    @MockBean
    private ErsRequestSender ersRequestSender;
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

    private Instances data;

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
        DecisionTreeOptions treeOptions = TestHelperUtils.createDecisionTreeOptions();
        treeOptions.setDecisionTreeType(DecisionTreeType.CART);
        decisionTreeOptions = objectMapper.writeValueAsString(treeOptions);
    }

    @Override
    public void deleteAll() {
        classifierOptionsRequestRepository.deleteAll();
        ersRequestRepository.deleteAll();
        evaluationLogRepository.deleteAll();
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
                    var instancesRequestDataModel = new InstancesRequestDataModel(UUID.randomUUID().toString(), md5Hash(data), data);
                    evaluationOptimizerService.evaluateWithOptimalClassifierOptions(instancesRequestDataModel);
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
