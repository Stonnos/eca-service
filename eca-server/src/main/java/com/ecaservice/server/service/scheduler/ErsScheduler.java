package com.ecaservice.server.service.scheduler;

import com.ecaservice.server.service.ers.ErsRedeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Ers requests scheduler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@ConditionalOnProperty(value = "${ers.redelivery}", havingValue = "true")
@RequiredArgsConstructor
public class ErsScheduler {

    private final ErsRedeliveryService ersRedeliveryService;

    /**
     * Retry to send ers requests.
     */
    @Scheduled(fixedDelayString = "${ers.redeliveryIntervalSeconds}000")
    public void resendErsRequests() {
        log.info("Starting ers requests resending job");
        ersRedeliveryService.resendErsRequests();
        log.info("Ers requests resending job has been finished");
    }
}
