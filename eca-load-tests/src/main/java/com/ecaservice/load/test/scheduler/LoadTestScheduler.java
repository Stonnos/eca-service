package com.ecaservice.load.test.scheduler;

import com.ecaservice.load.test.config.rabbit.QueueConfig;
import com.ecaservice.load.test.entity.EvaluationRequestEntity;
import com.ecaservice.load.test.entity.ExecutionStatus;
import com.ecaservice.load.test.entity.LoadTestEntity;
import com.ecaservice.load.test.entity.RequestStageType;
import com.ecaservice.load.test.repository.EvaluationRequestRepository;
import com.ecaservice.load.test.repository.LoadTestRepository;
import com.ecaservice.load.test.service.InstancesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

/**
 * Load tests scheduler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoadTestScheduler {

    private final QueueConfig queueConfig;
    private final InstancesService instancesService;
    private final RabbitTemplate rabbitTemplate;
    private final LoadTestRepository loadTestRepository;
    private final EvaluationRequestRepository evaluationRequestRepository;

    /**
     * Starts new tests.
     */
    @Scheduled(fixedDelayString = "${eca-load-tests.delaySeconds}000")
    public void startNewTests() {
        log.trace("Starting to start new tests");
        Page<LoadTestEntity> page = getNextTest();
        if (page == null || !page.hasContent()) {
            log.trace("Not one new test has been found");
            return;
        }
        startTest(page.getContent().iterator().next());
        log.trace("Finished new tests starting");
    }

    private Page<LoadTestEntity> getNextTest() {
        return loadTestRepository.findAllByExecutionStatusIn(Collections.singletonList(ExecutionStatus.NEW),
                PageRequest.of(0, 1));
    }

    private void startTest(LoadTestEntity loadTestEntity) {
        ThreadPoolTaskExecutor executor = initThreadPoolTaskExecutor(loadTestEntity.getNumThreads());
        for (int i = 0; i < loadTestEntity.getNumRequests(); i++) {
            executor.submit(() -> {
                String correlationId = UUID.randomUUID().toString();
                EvaluationRequestEntity evaluationRequestEntity = new EvaluationRequestEntity();
                evaluationRequestEntity.setCorrelationId(correlationId);
                evaluationRequestEntity.setLoadTestEntity(loadTestEntity);
                try {
                    rabbitTemplate.convertAndSend(queueConfig.getEvaluationRequestQueue(), "", message -> {
                        message.getMessageProperties().setCorrelationId(evaluationRequestEntity.getCorrelationId());
                        message.getMessageProperties().setReplyTo(queueConfig.getReplyToQueue());
                        return message;
                    });
                    evaluationRequestEntity.setStageType(RequestStageType.REQUEST_SENT);
                    log.info("Request with correlation id [{}] has been sent", correlationId);
                } catch (AmqpException ex) {
                    log.error("AMQP error while sending request with correlation id [{}]: {}", correlationId,
                            ex.getMessage());
                    evaluationRequestEntity.setStageType(RequestStageType.NOT_SEND);
                } catch (Exception ex) {
                    log.error("Unknown error while sending request with correlation id [{}]: {}", correlationId,
                            ex.getMessage());
                    evaluationRequestEntity.setStageType(RequestStageType.ERROR);
                } finally {
                    evaluationRequestEntity.setStarted(LocalDateTime.now());
                    evaluationRequestRepository.save(evaluationRequestEntity);
                }
            });
        }
    }

    private ThreadPoolTaskExecutor initThreadPoolTaskExecutor(int poolSize) {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(poolSize);
        threadPoolTaskExecutor.setMaxPoolSize(poolSize);
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }
}
