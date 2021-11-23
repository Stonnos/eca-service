package com.ecaservice.classifier.options.model;

import eca.metrics.distances.DistanceType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static com.ecaservice.classifier.options.model.Constraints.MAX_LENGTH_255;

/**
 * K - nearest neighbours input options.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "K - nearest neighbours classifier options")
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
    @Schema(description = "Distance function type", maxLength = MAX_LENGTH_255)
    private DistanceType distanceType;

}
