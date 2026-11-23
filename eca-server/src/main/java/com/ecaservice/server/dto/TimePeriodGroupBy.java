package com.ecaservice.server.dto;

/**
 * Time interval granularity for statistics aggregation.
 *
 * @author Roman Batygin
 */
public enum TimePeriodGroupBy {

    /**
     * Group by day
     */
    DAY,

    /**
     * Group by month
     */
    MONTH
}