package com.ecaservice.load.test.service.executor;

import com.ecaservice.base.model.EvaluationRequest;
import com.ecaservice.classifier.options.adapter.ClassifierOptionsAdapter;
import com.ecaservice.classifier.options.model.ClassifierOptions;
import com.ecaservice.load.test.config.EcaLoadTestsConfig;
import com.ecaservice.load.test.entity.EvaluationRequestEntity;
import com.ecaservice.load.test.entity.ExecutionStatus;
import com.ecaservice.load.test.entity.LoadTestEntity;
import com.ecaservice.load.test.entity.RequestStageType;
import com.ecaservice.load.test.entity.TestResult;
import com.ecaservice.load.test.mapping.LoadTestMapper;
import com.ecaservice.load.test.model.TestDataModel;
import com.ecaservice.load.test.repository.EvaluationRequestRepository;
import com.ecaservice.load.test.repository.LoadTestRepository;
import com.ecaservice.load.test.service.ClassifiersConfigService;
import com.ecaservice.load.test.service.InstancesConfigService;
import com.ecaservice.load.test.service.data.InstancesLoader;
import com.ecaservice.load.test.service.rabbit.RabbitSender;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;
import weka.core.Randomizable;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.ecaservice.load.test.util.Utils.createEvaluationRequestEntity;

/**
 * Abstract test executor.
 *
 * @author Roman Batygin
 */
@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractTestExecutor {

    protected final EcaLoadTestsConfig ecaLoadTestsConfig;
    private final InstancesConfigService instancesConfigService;
    private final ClassifiersConfigService classifiersConfigService;
    private final RabbitSender rabbitSender;
    private final ClassifierOptionsAdapter classifierOptionsAdapter;
    private final InstancesLoader instancesLoader;
    private final LoadTestMapper loadTestMapper;
    private final LoadTestRepository loadTestRepository;
    private final EvaluationRequestRepository evaluationRequestRepository;

    /**
     * Runs load test.
     *
     * @param loadTestEntity - load test entity
     */
    public void runTest(LoadTestEntity loadTestEntity) {
        log.info("Runs new test with uuid [{}]", loadTestEntity.getTestUuid());
        loadTestEntity.setStarted(LocalDateTime.now());
        loadTestEntity.setExecutionStatus(ExecutionStatus.IN_PROGRESS);
        loadTestRepository.save(loadTestEntity);
        sendRequests(loadTestEntity);
        log.info("Test [{}] has been started", loadTestEntity.getTestUuid());
    }

    /**
     * Gets test data iterator.
     *
     * @param loadTestEntity           - load test entity
     * @param instancesConfigService   - instances config service
     * @param classifiersConfigService - classifiers config service
     * @return test data iterator
     */
    protected abstract Iterator<TestDataModel> testDataIterator(LoadTestEntity loadTestEntity,
                                                                InstancesConfigService instancesConfigService,
                                                                ClassifiersConfigService classifiersConfigService);

    private void sendRequests(LoadTestEntity loadTestEntity) {
        ThreadPoolTaskExecutor executor = initializeThreadPoolTaskExecutor(loadTestEntity.getNumThreads());
        CountDownLatch countDownLatch = new CountDownLatch(loadTestEntity.getNumRequests());
        try {
            Iterator<TestDataModel> iterator =
                    testDataIterator(loadTestEntity, instancesConfigService, classifiersConfigService);
            while (iterator.hasNext()) {
                TestDataModel testDataModel = iterator.next();
                EvaluationRequest evaluationRequest = createEvaluationRequest(loadTestEntity, testDataModel);
                ClassifierOptions classifierOptions =
                        classifierOptionsAdapter.convert(evaluationRequest.getClassifier());
                EvaluationRequestEntity evaluationRequestEntity =
                        createEvaluationRequestEntity(loadTestEntity, classifierOptions, evaluationRequest.getData());
                evaluationRequestEntity.setTestResult(TestResult.UNKNOWN);
                evaluationRequestEntity.setClassifierName(evaluationRequest.getClassifier().getClass().getSimpleName());
                Runnable task = createTask(evaluationRequest, evaluationRequestEntity, countDownLatch);
                executor.submit(task);
            }
            if (!countDownLatch.await(ecaLoadTestsConfig.getWorkerThreadTimeOutInSeconds(), TimeUnit.SECONDS)) {
                log.warn("Worker thread timeout occurred for test [{}]", loadTestEntity.getTestUuid());
            }
        } catch (Exception ex) {
            log.error("There was an error while sending requests for test [{}]: {}", loadTestEntity.getTestUuid(),
                    ex.getMessage());
            failed(loadTestEntity, ex);
        } finally {
            executor.shutdown();
        }
    }

    private void failed(LoadTestEntity loadTestEntity, Exception ex) {
        loadTestEntity.setDetails(ex.getMessage());
        loadTestEntity.setExecutionStatus(ExecutionStatus.ERROR);
        loadTestRepository.save(loadTestEntity);
    }

    private EvaluationRequest createEvaluationRequest(LoadTestEntity loadTestEntity, TestDataModel testDataModel) {
        Instances instances = instancesLoader.loadInstances(testDataModel.getDataResource());
        AbstractClassifier classifier = initializeNextClassifier(testDataModel);
        EvaluationRequest evaluationRequest = loadTestMapper.map(loadTestEntity);
        evaluationRequest.setData(instances);
        evaluationRequest.setClassifier(classifier);
        return evaluationRequest;
    }

    private AbstractClassifier initializeNextClassifier(TestDataModel testDataModel) {
        ClassifierOptions classifierOptions = testDataModel.getClassifierOptions();
        AbstractClassifier classifier = classifierOptionsAdapter.convert(classifierOptions);
        if (classifier instanceof Randomizable) {
            ((Randomizable) classifier).setSeed(ecaLoadTestsConfig.getSeed());
        }
        return classifier;
    }

    private Runnable createTask(EvaluationRequest evaluationRequest,
                                EvaluationRequestEntity evaluationRequestEntity, CountDownLatch countDownLatch) {
        return () -> {
            try {
                evaluationRequestEntity.setStarted(LocalDateTime.now());
                rabbitSender.send(evaluationRequest, evaluationRequestEntity.getCorrelationId());
                evaluationRequestEntity.setStageType(RequestStageType.REQUEST_SENT);
                log.info("Request with correlation id [{}] has been sent",
                        evaluationRequestEntity.getCorrelationId());
            } catch (AmqpException ex) {
                log.error("AMQP error while sending request with correlation id [{}]: {}",
                        evaluationRequestEntity.getCorrelationId(),
                        ex.getMessage());
                handleErrorRequest(evaluationRequestEntity, RequestStageType.NOT_SEND);
            } catch (Exception ex) {
                log.error("Unknown error while sending request with correlation id [{}]: {}",
                        evaluationRequestEntity.getCorrelationId(),
                        ex.getMessage());
                handleErrorRequest(evaluationRequestEntity, RequestStageType.ERROR);
            } finally {
                evaluationRequestRepository.save(evaluationRequestEntity);
                countDownLatch.countDown();
            }
        };
    }

    private void handleErrorRequest(EvaluationRequestEntity evaluationRequestEntity,
                                    RequestStageType requestStageType) {
        evaluationRequestEntity.setTestResult(TestResult.ERROR);
        evaluationRequestEntity.setStageType(requestStageType);
        evaluationRequestEntity.setFinished(LocalDateTime.now());
    }

    private ThreadPoolTaskExecutor initializeThreadPoolTaskExecutor(int poolSize) {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(poolSize);
        threadPoolTaskExecutor.setMaxPoolSize(poolSize);
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }
}
