package com.ecaservice.core.transactional.outbox.scheduler;

import com.ecaservice.core.transactional.outbox.config.TransactionalOutboxProperties;
import com.ecaservice.core.transactional.outbox.service.OutboxMessageProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Implements scheduler for sent outbox messages.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@ConditionalOnProperty(value = "transactional.outbox.retry", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class OutboxMessageScheduler {

    private final TransactionalOutboxProperties transactionalOutboxProperties;
    private final OutboxMessageProcessor outboxMessageProcessor;
    private final ThreadPoolTaskScheduler retryRequestThreadPoolTaskScheduler;

    /**
     * Starts scheduler.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        log.info("Starting to initialize transactional outbox message job");
        retryRequestThreadPoolTaskScheduler.scheduleWithFixedDelay(
                outboxMessageProcessor::processNotSentMessages,
                Duration.ofMillis(transactionalOutboxProperties.getRetryIntervalMillis())
        );
        log.info("Transactional outbox message job has been started");
    }
}
