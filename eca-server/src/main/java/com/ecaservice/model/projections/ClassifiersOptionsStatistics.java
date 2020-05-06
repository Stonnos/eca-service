package com.ecaservice.model.projections;

/**
 * Classifiers options statistics projection interface.
 *
 * @author Roman Batygin
 */
public interface ClassifiersOptionsStatistics {

    /**
     * Gets classifiers configuration id
     *
     * @return classifiers configuration id
     */
    Long getConfigurationId();

    /**
     * Classifiers options count associated with configuration.
     *
     * @return classifiers options count
     */
    long getClassifiersOptionsCount();
}
