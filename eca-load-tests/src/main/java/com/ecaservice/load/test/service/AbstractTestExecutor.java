package com.ecaservice.load.test.service;

import com.ecaservice.base.model.EvaluationRequest;
import com.ecaservice.load.test.entity.EvaluationRequestEntity;
import com.ecaservice.load.test.entity.ExecutionStatus;
import com.ecaservice.load.test.entity.LoadTestEntity;
import com.ecaservice.load.test.entity.RequestStageType;
import com.ecaservice.load.test.mapping.EvaluationRequestMapper;
import com.ecaservice.load.test.repository.EvaluationRequestRepository;
import com.ecaservice.load.test.repository.LoadTestRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;

import static com.ecaservice.load.test.util.Utils.createEvaluationRequestEntity;

/**
 * Abstract test executor.
 *
 * @author Roman Batygin
 */
@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractTestExecutor {

    private final RabbitSender rabbitSender;
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
     * Gets next instances (training data set).
     *
     * @return instances object
     */
    protected abstract Instances getNextInstances();

    /**
     * Gets next classifier.
     *
     * @return classifier object
     */
    protected abstract AbstractClassifier getNextClassifier();

    private void sendRequests(LoadTestEntity loadTestEntity) {
        ThreadPoolTaskExecutor executor = initializeThreadPoolTaskExecutor(loadTestEntity.getNumThreads());
        CountDownLatch countDownLatch = new CountDownLatch(loadTestEntity.getNumRequests());
        try {
            for (int i = 0; i < loadTestEntity.getNumRequests(); i++) {
                executor.submit(createTask(loadTestEntity, countDownLatch));
            }
            countDownLatch.await();
            executor.shutdown();
        } catch (Exception ex) {
            log.error("There was an error while sending requests for test [{}]: {}", loadTestEntity.getTestUuid(),
                    ex.getMessage());
        }
    }

    private Runnable createTask(final LoadTestEntity loadTestEntity, final CountDownLatch countDownLatch) {
        final Instances instances = getNextInstances();
        final AbstractClassifier classifier = getNextClassifier();
        final EvaluationRequest evaluationRequest = evaluationRequestMapper.map(loadTestEntity);
        evaluationRequest.setData(instances);
        evaluationRequest.setClassifier(classifier);
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
                evaluationRequestEntity.setStageType(RequestStageType.NOT_SEND);
            } catch (Exception ex) {
                log.error("Unknown error while sending request with correlation id [{}]: {}",
                        evaluationRequestEntity.getCorrelationId(),
                        ex.getMessage());
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
