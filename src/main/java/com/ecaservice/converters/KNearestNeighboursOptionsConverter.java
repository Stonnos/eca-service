package com.ecaservice.converters;

import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.model.options.KNearestNeighboursOptions;
import eca.metrics.KNearestNeighbours;
import eca.metrics.distances.DistanceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Implements K - nearest neighbours options converted.
 *
 * @author Roman Batygin
 */
@Component
public class KNearestNeighboursOptionsConverter
        extends ClassifierOptionsConverter<KNearestNeighboursOptions, KNearestNeighbours> {

    private static final DistanceBuilder DISTANCE_BUILDER = new DistanceBuilder();

    private final ExperimentConfig experimentConfig;

    /**
     * Constructor with spring dependency injection.
     *
     * @param experimentConfig {@link ExperimentConfig} bean
     */
    @Autowired
    public KNearestNeighboursOptionsConverter(ExperimentConfig experimentConfig) {
        super(KNearestNeighboursOptions.class);
        this.experimentConfig = experimentConfig;
    }

    @Override
    public KNearestNeighbours convert(KNearestNeighboursOptions kNearestNeighboursOptions) {
        KNearestNeighbours kNearestNeighbours = new KNearestNeighbours();
        kNearestNeighbours.setNumNeighbours(kNearestNeighbours.getNumNeighbours());
        kNearestNeighbours.setWeight(kNearestNeighbours.getWeight());
        if (kNearestNeighboursOptions.getDistanceType() != null) {
            kNearestNeighbours.setDistance(kNearestNeighboursOptions.getDistanceType().handle(DISTANCE_BUILDER));
        }
        kNearestNeighbours.getDecimalFormat().setMaximumFractionDigits(experimentConfig.getMaximumFractionDigits());
        return kNearestNeighbours;
    }
}
