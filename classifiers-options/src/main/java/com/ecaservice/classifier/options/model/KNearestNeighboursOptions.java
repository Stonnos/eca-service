package com.ecaservice.classifier.options.model;

import eca.metrics.distances.DistanceType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * K - nearest neighbours input options.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class KNearestNeighboursOptions extends ClassifierOptions {

    /**
     * Neighbours number
     */
    private Integer numNeighbours;

    /**
     * Neighbour's weight value
     */
    private Double weight;

    /**
     * Distance function type
     */
    private DistanceType distanceType;

}
