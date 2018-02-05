package com.ecaservice.model.options;

import eca.metrics.distances.DistanceType;
import lombok.Data;

/**
 * K - nearest neighbours input options.
 *
 * @author Roman Batygin
 */
@Data
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
