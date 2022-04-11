package com.ecaservice.core.redelivery.scheduler;

import com.ecaservice.core.redelivery.config.RedeliveryProperties;
import com.ecaservice.core.redelivery.service.RequestRedeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

/**
 * Implements scheduler for retry requests mechanism.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RetryRequestScheduler {

    private final RedeliveryProperties redeliveryProperties;
    private final RequestRedeliveryService requestRedeliveryService;
    private final ThreadPoolTaskScheduler retryRequestThreadPoolTaskScheduler;

    /**
     * Starts scheduler.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        log.info("Starting to initialize retry request redelivery job");
        retryRequestThreadPoolTaskScheduler.scheduleWithFixedDelay(
                requestRedeliveryService::processNotSentRequests,
                redeliveryProperties.getRedeliveryIntervalMillis()
        );
        log.info("Retry request redelivery job has been started");
    }
}
