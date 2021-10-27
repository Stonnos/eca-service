package com.ecaservice.core.mail.client.scheduler;

import com.ecaservice.core.mail.client.config.EcaMailClientProperties;
import com.ecaservice.core.mail.client.service.EmailRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

/**
 * Email request scheduler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@ConditionalOnProperty(value = "mail.client.redelivery", havingValue = "true")
@RequiredArgsConstructor
public class EmailRequestScheduler {

    private final EcaMailClientProperties ecaMailClientProperties;
    private final ThreadPoolTaskScheduler mailThreadPoolTaskScheduler;
    private final EmailRequestService emailRequestService;

    /**
     * Starts email requests redelivery job.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        log.info("Starting to initialize email requests redelivery job");
        mailThreadPoolTaskScheduler.scheduleWithFixedDelay(emailRequestService::processNotSentEmailRequests,
                ecaMailClientProperties.getRedeliveryIntervalMillis());
        mailThreadPoolTaskScheduler.scheduleWithFixedDelay(emailRequestService::processExceededEmailRequests,
                ecaMailClientProperties.getRedeliveryIntervalMillis());
        log.info("Email requests redelivery job has been started");
    }
}
