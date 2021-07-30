package com.ecaservice.load.test.service.executor;

import com.ecaservice.base.model.EvaluationRequest;
import com.ecaservice.classifier.options.adapter.ClassifierOptionsAdapter;
import com.ecaservice.classifier.options.model.ClassifierOptions;
import com.ecaservice.load.test.config.EcaLoadTestsConfig;
import com.ecaservice.load.test.entity.EvaluationRequestEntity;
import com.ecaservice.load.test.entity.LoadTestEntity;
import com.ecaservice.load.test.mapping.LoadTestMapper;
import com.ecaservice.load.test.model.TestDataModel;
import com.ecaservice.load.test.repository.EvaluationRequestRepository;
import com.ecaservice.load.test.repository.LoadTestRepository;
import com.ecaservice.load.test.service.ClassifiersTestDataProvider;
import com.ecaservice.load.test.service.InstancesTestDataProvider;
import com.ecaservice.load.test.service.LoadTestDataIterator;
import com.ecaservice.load.test.service.TestWorkerService;
import com.ecaservice.test.common.model.ExecutionStatus;
import com.ecaservice.test.common.service.InstancesLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;
import weka.core.Randomizable;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.ecaservice.load.test.util.Utils.createEvaluationRequestEntity;

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
    private final InstancesTestDataProvider instancesTestDataProvider;
    private final ClassifiersTestDataProvider classifiersTestDataProvider;
    private final TestWorkerService testWorkerService;
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
                EvaluationRequest evaluationRequest = createEvaluationRequest(loadTestEntity, testDataModel);
                ClassifierOptions classifierOptions =
                        classifierOptionsAdapter.convert(evaluationRequest.getClassifier());
                EvaluationRequestEntity evaluationRequestEntity =
                        createAndSaveEvaluationRequest(loadTestEntity, classifierOptions, evaluationRequest);
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

    private EvaluationRequestEntity createAndSaveEvaluationRequest(LoadTestEntity loadTestEntity,
                                                                   ClassifierOptions classifierOptions,
                                                                   EvaluationRequest evaluationRequest) {
        EvaluationRequestEntity evaluationRequestEntity =
                createEvaluationRequestEntity(loadTestEntity, classifierOptions, evaluationRequest);
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
