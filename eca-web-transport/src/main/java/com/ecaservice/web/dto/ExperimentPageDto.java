package com.ecaservice.web.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Experiment page dto model.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExperimentPageDto extends PageDto<ExperimentDto> {

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
