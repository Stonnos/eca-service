package com.ecaservice.model.projections;

import com.ecaservice.model.experiment.ExperimentType;

/**
 * Experiment types count statistics projection interface.
 *
 * @author Roman Batygin
 */
public interface ExperimentTypeStatistics {

    /**
     * Gets experiment type.
     *
     * @return experiment type
     */
    ExperimentType getExperimentType();

    /**
     * Gets experiments count with specified type.
     *
     * @return experiments count with specified type
     */
    long getExperimentsCount();
}
