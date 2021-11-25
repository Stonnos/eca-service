package com.ecaservice.classifier.options.model;

import eca.metrics.distances.DistanceType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import static com.ecaservice.classifier.options.model.Constraints.DECIMAL_VALUE_0_5_STRING;
import static com.ecaservice.classifier.options.model.Constraints.DECIMAL_VALUE_1_STRING;
import static com.ecaservice.classifier.options.model.Constraints.MAX_LENGTH_255;
import static com.ecaservice.classifier.options.model.Constraints.VALUE_1;

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
    @Min(VALUE_1)
    @Max(Integer.MAX_VALUE)
    @Schema(description = "Neighbours number")
    private Integer numNeighbours;

    /**
     * Neighbour's weight value
     */
    @DecimalMin(DECIMAL_VALUE_0_5_STRING)
    @DecimalMax(DECIMAL_VALUE_1_STRING)
    @Schema(description = "Neighbour's weight value")
    private Double weight;

    /**
     * Distance function type
     */
    @Schema(description = "Distance function type", maxLength = MAX_LENGTH_255)
    private DistanceType distanceType;

}
