package com.ecaservice.server.model;

import java.time.LocalDateTime;

/**
 * Time-series evaluation log counting statistics projection.
 *
 * @author Roman Batygin
 */
public interface TimeSeriesStatistics {

    /**
     * Gets period start.
     *
     * @return period start
     */
    LocalDateTime getPeriodStart();

    /**
     * Gets evaluation requests count for the period.
     *
     * @return requests count
     */
    Long getRequestsCount();
}