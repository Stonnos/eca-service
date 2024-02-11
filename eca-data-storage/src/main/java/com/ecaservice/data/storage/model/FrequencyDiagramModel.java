package com.ecaservice.data.storage.model;

import lombok.Data;

/**
 * Frequency diagram data model.
 *
 * @author Roman Batygin
 */
@Data
public class FrequencyDiagramModel {

    /**
     * Frequency value
     */
    private int frequency;

    /**
     * Lower bound
     */
    private double lowerBound;

    /**
     * Upper bound
     */
    private double upperBound;
}
