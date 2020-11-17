package com.ecaservice.external.api.metrics;

import com.ecaservice.external.api.dto.RequestStatus;
import com.ecaservice.external.api.entity.EcaRequestEntity;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.stream.Stream;

import static com.ecaservice.external.api.config.metrics.MetricConstants.REQUESTS_METRIC;
import static com.ecaservice.external.api.config.metrics.MetricConstants.REQUESTS_TOTAL_METRIC;
import static com.ecaservice.external.api.config.metrics.MetricConstants.REQUEST_DURATION_METRIC;
import static com.ecaservice.external.api.config.metrics.MetricConstants.REQUEST_STATUS_TAG;
import static com.ecaservice.external.api.config.metrics.MetricConstants.RESPONSES_TOTAL_METRIC;
import static com.google.common.collect.Maps.newHashMap;

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

    private Counter requestDurationCounter;
    private Counter requestsTotalCounter;
    private Counter responsesTotalCounter;

    private Map<RequestStatus, Counter> requestStatusCounterMap = newHashMap();

    /**
     * Initialize metrics.
     */
    @PostConstruct
    public void init() {
        requestDurationCounter = meterRegistry.counter(REQUEST_DURATION_METRIC);
        requestsTotalCounter = meterRegistry.counter(REQUESTS_TOTAL_METRIC);
        responsesTotalCounter = meterRegistry.counter(RESPONSES_TOTAL_METRIC);
        configureRequestStatusCountersMap();
    }

    /**
     * Tracks request duration.
     *
     * @param duration - request duration in millis
     */
    public void trackRequestDuration(long duration) {
        requestDurationCounter.increment(duration);
    }

    /**
     * Tracks total requests
     */
    public void trackRequestsTotal() {
        requestsTotalCounter.increment();
    }

    /**
     * Tracks total responses
     */
    public void trackResponsesTotal() {
        responsesTotalCounter.increment();
    }

    /**
     * Tracks response.
     *
     * @param ecaRequestEntity - eca request entity
     * @param requestStatus    - request status
     */
    public void trackResponse(EcaRequestEntity ecaRequestEntity, RequestStatus requestStatus) {
        trackRequestStatus(requestStatus);
        long duration = ChronoUnit.MILLIS.between(ecaRequestEntity.getCreationDate(), ecaRequestEntity.getEndDate());
        trackRequestDuration(duration);
        trackResponsesTotal();
    }

    /**
     * Tracks request status.
     *
     * @param requestStatus - request status
     */
    public void trackRequestStatus(RequestStatus requestStatus) {
        Counter counter = requestStatusCounterMap.get(requestStatus);
        if (counter == null) {
            log.warn("Counter not specified for request status [{}]", requestStatus);
        } else {
            counter.increment();
        }
    }

    private void configureRequestStatusCountersMap() {
        Stream.of(RequestStatus.values()).forEach(requestStatus -> {
            Counter counter = meterRegistry.counter(REQUESTS_METRIC, REQUEST_STATUS_TAG, requestStatus.name());
            requestStatusCounterMap.put(requestStatus, counter);
        });
    }
}
