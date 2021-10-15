package com.ecaservice.classifier.options.model;

import eca.metrics.distances.DistanceType;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "Neighbours number")
    private Integer numNeighbours;

    /**
     * Neighbour's weight value
     */
    @Schema(description = "Neighbour's weight value")
    private Double weight;

    /**
     * Distance function type
     */
    @Schema(description = "Distance function type")
    private DistanceType distanceType;

}
