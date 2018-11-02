package com.ecaservice.web.dto;

import lombok.Data;

/**
 * Experiment page dto model.
 *
 * @author Roman Batygin
 */
@Data
public class ExperimentStatisticsDto {

    /**
     * Total experiments count
     */
    private long totalCount;

    /**
     * Experiments count with NEW status
     */
    private long newExperimentsCount;

    /**
     * Experiments count with FINISHED status
     */
    private long finishedExperimentsCount;

    /**
     * Experiments count with TIMEOUT status
     */
    private long timeoutExperimentsCount;

    /**
     * Experiments count with ERROR status
     */
    private long errorExperimentsCount;
}
