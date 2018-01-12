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

    private Integer numNeighbours;

    private Double weight;

    private DistanceType distanceType;

    private Integer maximumFractionDigits;
}
