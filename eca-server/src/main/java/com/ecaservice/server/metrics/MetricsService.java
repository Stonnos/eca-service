package com.ecaservice.server.metrics;

import com.ecaservice.base.model.ErrorCode;
import com.ecaservice.server.model.entity.RequestStatus;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.ecaservice.server.metrics.MetricConstants.EVALUATION_MQ_REQUEST_ERROR_METRIC;
import static com.ecaservice.server.metrics.MetricConstants.EVALUATION_REQUEST_STATUS_TOTAL_METRIC;
import static com.ecaservice.server.metrics.MetricConstants.EVALUATION_REQUEST_TOTAL_METRIC;
import static com.ecaservice.server.metrics.MetricConstants.EXPERIMENT_REQUEST_STATUS_TOTAL_METRIC;
import static com.ecaservice.server.metrics.MetricConstants.EXPERIMENT_REQUEST_TOTAL_METRIC;

/**
 * Metrics service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MetricsService {

    private static final String ERROR_TAG = "error";
    private static final String STATUS_TAG = "status";

    private final MeterRegistry meterRegistry;

    /**
     * Track evaluation requests total.
     */
    public void trackEvaluationMqRequestError(ErrorCode errorCode) {
        var counter = meterRegistry.counter(EVALUATION_MQ_REQUEST_ERROR_METRIC, ERROR_TAG, errorCode.name());
        counter.increment();
    }

    /**
     * Track evaluation requests total.
     */
    public void trackEvaluationRequest() {
        var counter = meterRegistry.counter(EVALUATION_REQUEST_TOTAL_METRIC);
        counter.increment();
    }

    /**
     * Track experiment requests total.
     */
    public void trackExperimentRequest() {
        var counter = meterRegistry.counter(EXPERIMENT_REQUEST_TOTAL_METRIC);
        counter.increment();
    }

    /**
     * Track evaluation requests status total.
     *
     * @param requestStatus - request status
     */
    public void trackEvaluationRequestStatus(RequestStatus requestStatus) {
        var counter = meterRegistry.counter(EVALUATION_REQUEST_STATUS_TOTAL_METRIC, STATUS_TAG, requestStatus.name());
        counter.increment();
    }

    /**
     * Track experiment requests status total.
     *
     * @param requestStatus - request status
     */
    public void trackExperimentRequestStatus(RequestStatus requestStatus) {
        var counter = meterRegistry.counter(EXPERIMENT_REQUEST_STATUS_TOTAL_METRIC, STATUS_TAG, requestStatus.name());
        counter.increment();
    }
}
