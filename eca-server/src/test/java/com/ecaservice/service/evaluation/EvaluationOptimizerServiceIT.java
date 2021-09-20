package com.ecaservice.service.evaluation;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.base.model.InstancesRequest;
import com.ecaservice.classifier.options.config.ClassifiersOptionsAutoConfiguration;
import com.ecaservice.classifier.options.model.DecisionTreeOptions;
import com.ecaservice.config.AppProperties;
import com.ecaservice.config.CrossValidationConfig;
import com.ecaservice.config.ers.ErsConfig;
import com.ecaservice.configuation.ExecutorConfiguration;
import com.ecaservice.core.lock.redis.annotation.EnableRedisLocks;
import com.ecaservice.ers.dto.ClassifierOptionsRequest;
import com.ecaservice.ers.dto.ClassifierOptionsResponse;
import com.ecaservice.ers.dto.ResponseStatus;
import com.ecaservice.mapping.ClassifierInfoMapperImpl;
import com.ecaservice.mapping.ClassifierOptionsRequestMapperImpl;
import com.ecaservice.mapping.ClassifierOptionsRequestModelMapperImpl;
import com.ecaservice.mapping.ClassifierOptionsResponseModelMapperImpl;
import com.ecaservice.mapping.ClassifierReportMapperImpl;
import com.ecaservice.mapping.DateTimeConverter;
import com.ecaservice.mapping.ErsEvaluationMethodMapperImpl;
import com.ecaservice.mapping.ErsResponseStatusMapperImpl;
import com.ecaservice.mapping.EvaluationLogMapperImpl;
import com.ecaservice.mapping.EvaluationRequestMapperImpl;
import com.ecaservice.mapping.InstancesConverter;
import com.ecaservice.mapping.InstancesInfoMapperImpl;
import com.ecaservice.model.entity.ClassifierOptionsRequestEntity;
import com.ecaservice.model.evaluation.ClassifierOptionsRequestSource;
import com.ecaservice.repository.ClassifierOptionsRequestModelRepository;
import com.ecaservice.repository.ClassifierOptionsRequestRepository;
import com.ecaservice.repository.ErsRequestRepository;
import com.ecaservice.repository.EvaluationLogRepository;
import com.ecaservice.service.AbstractJpaTest;
import com.ecaservice.service.ers.ErsRequestSender;
import com.ecaservice.service.ers.ErsRequestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import eca.ensemble.forests.DecisionTreeType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.DockerComposeContainer;

import javax.inject.Inject;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link EvaluationOptimizerService} functionality.
 *
 * @author Roman Batygin
 */
@EnableAspectJAutoProxy
@EnableRedisLocks
@Import({ExecutorConfiguration.class, ClassifiersOptionsAutoConfiguration.class, AppProperties.class,
        CrossValidationConfig.class, EvaluationRequestService.class, InstancesInfoMapperImpl.class,
        ClassifierOptionsRequestModelMapperImpl.class, ClassifierReportMapperImpl.class,
        EvaluationRequestMapperImpl.class, ClassifierOptionsRequestMapperImpl.class,
        ErsConfig.class, EvaluationLogMapperImpl.class,
        EvaluationService.class, ErsEvaluationMethodMapperImpl.class, ErsResponseStatusMapperImpl.class,
        InstancesConverter.class, ClassifierOptionsResponseModelMapperImpl.class, ErsRequestService.class,
        EvaluationOptimizerService.class, ClassifierInfoMapperImpl.class, RedisAutoConfiguration.class,
        ClassifierOptionsCacheService.class, DateTimeConverter.class})
class EvaluationOptimizerServiceIT extends AbstractJpaTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final int NUM_THREADS = 2;

    @MockBean
    private ErsRequestSender ersRequestSender;
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
        instancesRequest = new InstancesRequest();
        instancesRequest.setData(TestHelperUtils.loadInstances());
        DecisionTreeOptions treeOptions = TestHelperUtils.createDecisionTreeOptions();
        treeOptions.setDecisionTreeType(DecisionTreeType.CART);
        decisionTreeOptions = objectMapper.writeValueAsString(treeOptions);
    }

    @Override
    public void deleteAll() {
        classifierOptionsRequestRepository.deleteAll();
        ersRequestRepository.deleteAll();
        evaluationLogRepository.deleteAll();
    }

    @Test
    void testClassifierOptionsCacheInMultiThreadEnvironment() throws Exception {
        ClassifierOptionsResponse response = TestHelperUtils.createClassifierOptionsResponse(Collections
                .singletonList(TestHelperUtils.createClassifierReport(decisionTreeOptions)), ResponseStatus.SUCCESS);
        when(ersRequestSender.getClassifierOptions(any(ClassifierOptionsRequest.class))).thenReturn(response);
        final CountDownLatch finishedLatch = new CountDownLatch(NUM_THREADS);
        ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);
        for (int i = 0; i < NUM_THREADS; i++) {
            executorService.submit(() -> {
                try {
                    evaluationOptimizerService.evaluateWithOptimalClassifierOptions(instancesRequest);
                } finally {
                    finishedLatch.countDown();
                }
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
}
