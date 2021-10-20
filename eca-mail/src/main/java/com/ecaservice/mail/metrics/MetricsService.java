package com.ecaservice.mail.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import static com.ecaservice.mail.config.metrics.MetricConstants.SENDING_EMAIL_MESSAGE_ERRORS_METRIC;
import static com.ecaservice.mail.config.metrics.MetricConstants.SENDING_EMAIL_MESSAGE_SUCCESS_METRIC;

/**
 * Metrics service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MetricsService {

    private final MeterRegistry meterRegistry;

    private Counter sendingEmailMessageErrorTotalCounter;
    private Counter sendingEmailMessageSuccessTotalCounter;

    /**
     * Initialize metrics.
     */
    @PostConstruct
    public void init() {
        sendingEmailMessageErrorTotalCounter = meterRegistry.counter(SENDING_EMAIL_MESSAGE_ERRORS_METRIC);
        sendingEmailMessageSuccessTotalCounter = meterRegistry.counter(SENDING_EMAIL_MESSAGE_SUCCESS_METRIC);
        log.info("Email metrics has been initialized");
    }

    /**
     * Tracks sending email message error total counter.
     */
    public void trackSendingEmailMessageErrorTotalCounter() {
        sendingEmailMessageErrorTotalCounter.increment();
    }

    /**
     * Tracks sending email message success total counter.
     */
    public void trackSendingEmailMessageSuccessTotalCounter() {
        sendingEmailMessageSuccessTotalCounter.increment();
    }
}
