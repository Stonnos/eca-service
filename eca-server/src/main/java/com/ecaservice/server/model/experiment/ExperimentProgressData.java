package com.ecaservice.server.model.experiment;

import lombok.Data;

/**
 * Experiment process data.
 *
 * @author Roman Batygin
 */
@Data
public class ExperimentProgressData {

    /**
     * Is experiment processing finished?
     */
    private boolean finished;

    /**
     * Is experiment processing canceled?
     */
    private boolean canceled;

    /**
     * Progress bar value in percentage
     */
    private Integer progress;

    /**
     * Experiment id
     */
    private Long experimentId;


}
