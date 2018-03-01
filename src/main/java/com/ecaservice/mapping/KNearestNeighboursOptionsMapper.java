package com.ecaservice.mapping;

import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.model.options.KNearestNeighboursOptions;
import eca.metrics.KNearestNeighbours;
import eca.metrics.distances.DistanceBuilder;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import javax.inject.Inject;

/**
 * Implements k - nearest neighbours input options mapping to k - nearest neighbours model.
 *
 * @author Roman Batygin
 */
@Mapper
public abstract class KNearestNeighboursOptionsMapper
        extends ClassifierOptionsMapper<KNearestNeighboursOptions, KNearestNeighbours> {

    @Inject
    private ExperimentConfig experimentConfig;

    protected KNearestNeighboursOptionsMapper() {
        super(KNearestNeighboursOptions.class);
    }

    @AfterMapping
    protected void postMapping(KNearestNeighboursOptions kNearestNeighboursOptions,
                               @MappingTarget KNearestNeighbours kNearestNeighbours) {
        if (kNearestNeighboursOptions.getDistanceType() != null) {
            kNearestNeighbours.setDistance(kNearestNeighboursOptions.getDistanceType().handle(new DistanceBuilder()));
        }
        if (experimentConfig.getMaximumFractionDigits() != null) {
            kNearestNeighbours.getDecimalFormat().setMaximumFractionDigits(
                    experimentConfig.getMaximumFractionDigits());
        }
    }
}
