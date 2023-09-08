package com.ecaservice.load.test.service.executor;

import com.ecaservice.base.model.EvaluationRequest;
import com.ecaservice.classifier.options.model.ClassifierOptions;
import com.ecaservice.classifier.options.model.RandomizeOptions;
import com.ecaservice.load.test.config.EcaLoadTestsConfig;
import com.ecaservice.load.test.entity.EvaluationRequestEntity;
import com.ecaservice.load.test.entity.LoadTestEntity;
import com.ecaservice.load.test.entity.RequestStageType;
import com.ecaservice.load.test.mapping.LoadTestMapper;
import com.ecaservice.load.test.model.TestDataModel;
import com.ecaservice.load.test.repository.EvaluationRequestRepository;
import com.ecaservice.load.test.repository.LoadTestRepository;
import com.ecaservice.load.test.service.ClassifiersTestDataProvider;
import com.ecaservice.load.test.service.InstancesTestDataProvider;
import com.ecaservice.load.test.service.LoadTestDataIterator;
import com.ecaservice.load.test.service.TestWorkerService;
import com.ecaservice.test.common.model.ExecutionStatus;
import com.ecaservice.test.common.model.TestResult;
import com.ecaservice.test.common.service.DataLoaderService;
import com.ecaservice.test.common.service.InstancesResourceLoader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import weka.core.Instances;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Abstract test executor.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TestExecutor {

    private final EcaLoadTestsConfig ecaLoadTestsConfig;
    private final ObjectMapper objectMapper;
    private final InstancesTestDataProvider instancesTestDataProvider;
    private final ClassifiersTestDataProvider classifiersTestDataProvider;
    private final TestWorkerService testWorkerService;
    private final DataLoaderService dataLoaderService;
    private final InstancesResourceLoader instancesResourceLoader;
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

    private Iterator<TestDataModel> testDataIterator(LoadTestEntity loadTestEntity,
                                                     InstancesTestDataProvider instancesTestDataProvider,
                                                     ClassifiersTestDataProvider classifiersTestDataProvider) {
        Random sampleRandom = new Random(ecaLoadTestsConfig.getSeed());
        Random classifiersRandom = new Random(ecaLoadTestsConfig.getSeed());
        return new LoadTestDataIterator(loadTestEntity, sampleRandom, classifiersRandom, instancesTestDataProvider,
                classifiersTestDataProvider);
    }

    private void sendRequests(LoadTestEntity loadTestEntity) {
        ThreadPoolTaskExecutor executor = initializeThreadPoolTaskExecutor(loadTestEntity.getNumThreads());
        CountDownLatch countDownLatch = new CountDownLatch(loadTestEntity.getNumRequests());
        try {
            Iterator<TestDataModel> iterator =
                    testDataIterator(loadTestEntity, instancesTestDataProvider, classifiersTestDataProvider);
            while (iterator.hasNext()) {
                TestDataModel testDataModel = iterator.next();
                String dataUuid = dataLoaderService.uploadInstances(testDataModel.getDataResource());
                EvaluationRequest evaluationRequest = createEvaluationRequest(loadTestEntity, testDataModel, dataUuid);
                EvaluationRequestEntity evaluationRequestEntity =
                        createAndSaveEvaluationRequest(loadTestEntity, testDataModel, evaluationRequest);
                Runnable task = createTask(evaluationRequestEntity.getId(), evaluationRequest, countDownLatch);
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
        loadTestEntity.setFinished(LocalDateTime.now());
        loadTestRepository.save(loadTestEntity);
    }

    private EvaluationRequest createEvaluationRequest(LoadTestEntity loadTestEntity, TestDataModel testDataModel,
                                                      String dataUuid) {
        initializeClassifierOptions(testDataModel);
        EvaluationRequest evaluationRequest = loadTestMapper.map(loadTestEntity);
        evaluationRequest.setDataUuid(dataUuid);
        evaluationRequest.setClassifierOptions(testDataModel.getClassifierOptions());
        return evaluationRequest;
    }

    private void initializeClassifierOptions(TestDataModel testDataModel) {
        ClassifierOptions classifierOptions = testDataModel.getClassifierOptions();
        if (classifierOptions instanceof RandomizeOptions) {
            ((RandomizeOptions) classifierOptions).setSeed(ecaLoadTestsConfig.getSeed());
        }
    }

    private EvaluationRequestEntity createAndSaveEvaluationRequest(LoadTestEntity loadTestEntity,
                                                                   TestDataModel testDataModel,
                                                                   EvaluationRequest evaluationRequest)
            throws JsonProcessingException {
        EvaluationRequestEntity evaluationRequestEntity = new EvaluationRequestEntity();
        evaluationRequestEntity.setCorrelationId(UUID.randomUUID().toString());
        evaluationRequestEntity.setStageType(RequestStageType.READY);
        evaluationRequestEntity.setTestResult(TestResult.UNKNOWN);
        evaluationRequestEntity.setLoadTestEntity(loadTestEntity);
        evaluationRequestEntity.setClassifierOptions(
                objectMapper.writeValueAsString(evaluationRequest.getClassifierOptions()));
        Instances instances = instancesResourceLoader.loadInstances(testDataModel.getDataResource());
        evaluationRequestEntity.setRelationName(instances.relationName());
        evaluationRequestEntity.setNumAttributes(instances.numAttributes());
        evaluationRequestEntity.setNumInstances(instances.numInstances());
        evaluationRequestEntity.setClassifierName(evaluationRequest.getClassifierOptions().getClass().getSimpleName());
        return evaluationRequestRepository.save(evaluationRequestEntity);
    }

    private Runnable createTask(long testId, EvaluationRequest evaluationRequest, CountDownLatch countDownLatch) {
        return () -> testWorkerService.sendRequest(testId, evaluationRequest, countDownLatch);
    }

    private ThreadPoolTaskExecutor initializeThreadPoolTaskExecutor(int poolSize) {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(poolSize);
        threadPoolTaskExecutor.setMaxPoolSize(poolSize);
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }
}
