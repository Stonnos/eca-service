package com.ecaservice.external.api.metrics;

import com.ecaservice.external.api.dto.ResponseCode;
import com.ecaservice.external.api.entity.EcaRequestEntity;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.stream.Stream;

import static com.ecaservice.external.api.config.metrics.MetricConstants.RESPONSES_METRIC;
import static com.ecaservice.external.api.config.metrics.MetricConstants.REQUESTS_TOTAL_METRIC;
import static com.ecaservice.external.api.config.metrics.MetricConstants.REQUEST_DURATION_METRIC;
import static com.ecaservice.external.api.config.metrics.MetricConstants.RESPONSES_TOTAL_METRIC;
import static com.ecaservice.external.api.config.metrics.MetricConstants.RESPONSE_CODE_TAG;
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

    private Map<ResponseCode, Counter> responseCodeCounterMap = newHashMap();

    /**
     * Initialize metrics.
     */
    @PostConstruct
    public void init() {
        requestDurationCounter = meterRegistry.counter(REQUEST_DURATION_METRIC);
        requestsTotalCounter = meterRegistry.counter(REQUESTS_TOTAL_METRIC);
        responsesTotalCounter = meterRegistry.counter(RESPONSES_TOTAL_METRIC);
        configureResponseCodeCountersMap();
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
     * @param responseCode     - response code
     */
    public void trackResponse(EcaRequestEntity ecaRequestEntity, ResponseCode responseCode) {
        trackResponseCode(responseCode);
        long duration = ChronoUnit.MILLIS.between(ecaRequestEntity.getCreationDate(), LocalDateTime.now());
        trackRequestDuration(duration);
        trackResponsesTotal();
    }

    /**
     * Tracks response code.
     *
     * @param responseCode - response code
     */
    public void trackResponseCode(ResponseCode responseCode) {
        Counter counter = responseCodeCounterMap.get(responseCode);
        if (counter == null) {
            log.warn("Counter not specified for response code [{}]", responseCode);
        } else {
            counter.increment();
        }
    }

    private void configureResponseCodeCountersMap() {
        Stream.of(ResponseCode.values()).forEach(responseCode -> {
            Counter counter = meterRegistry.counter(RESPONSES_METRIC, RESPONSE_CODE_TAG, responseCode.name());
            responseCodeCounterMap.put(responseCode, counter);
        });
    }
}
