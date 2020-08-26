package com.ecaservice.load.test.service;

import com.ecaservice.base.model.EvaluationRequest;
import com.ecaservice.classifier.options.adapter.ClassifierOptionsAdapter;
import com.ecaservice.classifier.options.model.ClassifierOptions;
import com.ecaservice.load.test.config.EcaLoadTestsConfig;
import com.ecaservice.load.test.entity.EvaluationRequestEntity;
import com.ecaservice.load.test.entity.ExecutionStatus;
import com.ecaservice.load.test.entity.LoadTestEntity;
import com.ecaservice.load.test.entity.RequestStageType;
import com.ecaservice.load.test.entity.TestResult;
import com.ecaservice.load.test.mapping.EvaluationRequestMapper;
import com.ecaservice.load.test.repository.EvaluationRequestRepository;
import com.ecaservice.load.test.repository.LoadTestRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

import java.time.LocalDateTime;
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
    private final RabbitSender rabbitSender;
    private final ClassifierOptionsAdapter classifierOptionsAdapter;
    private final InstancesLoader instancesLoader;
    private final EvaluationRequestMapper evaluationRequestMapper;
    private final LoadTestRepository loadTestRepository;
    private final EvaluationRequestRepository evaluationRequestRepository;

    public void runTest(LoadTestEntity loadTestEntity) {
        log.info("Runs new test with uuid [{}]", loadTestEntity.getTestUuid());
        loadTestEntity.setStarted(LocalDateTime.now());
        loadTestEntity.setExecutionStatus(ExecutionStatus.IN_PROGRESS);
        loadTestRepository.save(loadTestEntity);
        sendRequests(loadTestEntity);
        log.info("Test [{}] has been started", loadTestEntity.getTestUuid());
    }

    /**
     * Gets next instances (training data set) resource.
     *
     * @return instances resource
     */
    protected abstract Resource getNextSample();

    /**
     * Gets next classifier.
     *
     * @return classifier object
     */
    protected abstract ClassifierOptions getNextClassifierOptions();

    private void sendRequests(LoadTestEntity loadTestEntity) {
        ThreadPoolTaskExecutor executor = initializeThreadPoolTaskExecutor(loadTestEntity.getNumThreads());
        CountDownLatch countDownLatch = new CountDownLatch(loadTestEntity.getNumRequests());
        try {
            for (int i = 0; i < loadTestEntity.getNumRequests(); i++) {
                executor.submit(createTask(loadTestEntity, countDownLatch));
            }
            countDownLatch.await(ecaLoadTestsConfig.getWorkerThreadTimeOutInSeconds(), TimeUnit.SECONDS);
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

    private EvaluationRequest createEvaluationRequest(LoadTestEntity loadTestEntity) {
        Resource resource = getNextSample();
        Instances instances = instancesLoader.loadInstances(resource);
        ClassifierOptions classifierOptions = getNextClassifierOptions();
        AbstractClassifier classifier = classifierOptionsAdapter.convert(classifierOptions);
        EvaluationRequest evaluationRequest = evaluationRequestMapper.map(loadTestEntity);
        evaluationRequest.setData(instances);
        evaluationRequest.setClassifier(classifier);
        return evaluationRequest;
    }

    private Runnable createTask(final LoadTestEntity loadTestEntity, final CountDownLatch countDownLatch) {
        final EvaluationRequest evaluationRequest = createEvaluationRequest(loadTestEntity);
        return () -> {
            EvaluationRequestEntity evaluationRequestEntity = createEvaluationRequestEntity(loadTestEntity);
            try {
                rabbitSender.send(evaluationRequest, evaluationRequestEntity.getCorrelationId());
                evaluationRequestEntity.setStageType(RequestStageType.REQUEST_SENT);
                log.info("Request with correlation id [{}] has been sent",
                        evaluationRequestEntity.getCorrelationId());
            } catch (AmqpException ex) {
                log.error("AMQP error while sending request with correlation id [{}]: {}",
                        evaluationRequestEntity.getCorrelationId(),
                        ex.getMessage());
                evaluationRequestEntity.setTestResult(TestResult.ERROR);
                evaluationRequestEntity.setStageType(RequestStageType.NOT_SEND);
            } catch (Exception ex) {
                log.error("Unknown error while sending request with correlation id [{}]: {}",
                        evaluationRequestEntity.getCorrelationId(),
                        ex.getMessage());
                evaluationRequestEntity.setTestResult(TestResult.ERROR);
                evaluationRequestEntity.setStageType(RequestStageType.ERROR);
            } finally {
                evaluationRequestEntity.setStarted(LocalDateTime.now());
                evaluationRequestRepository.save(evaluationRequestEntity);
                countDownLatch.countDown();
            }
        };
    }

    private ThreadPoolTaskExecutor initializeThreadPoolTaskExecutor(int poolSize) {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(poolSize);
        threadPoolTaskExecutor.setMaxPoolSize(poolSize);
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }
}
