package com.ecaservice.classifier.options.mapping;

import com.ecaservice.classifier.options.model.KNearestNeighboursOptions;
import eca.metrics.KNearestNeighbours;
import eca.metrics.distances.DistanceBuilder;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.core.annotation.Order;

/**
 * Implements k - nearest neighbours input options mapping to k - nearest neighbours model.
 *
 * @author Roman Batygin
 */
@Order(Ordered.KNN_ORDER)
@Mapper
public abstract class KNearestNeighboursOptionsMapper
        extends ClassifierOptionsMapper<KNearestNeighboursOptions, KNearestNeighbours> {

    private static final DistanceBuilder DISTANCE_BUILDER = new DistanceBuilder();

    protected KNearestNeighboursOptionsMapper() {
        super(KNearestNeighboursOptions.class);
    }

    @AfterMapping
    protected void postMapping(KNearestNeighboursOptions kNearestNeighboursOptions,
                               @MappingTarget KNearestNeighbours kNearestNeighbours) {
        if (kNearestNeighboursOptions.getDistanceType() != null) {
            kNearestNeighbours.setDistance(kNearestNeighboursOptions.getDistanceType().handle(DISTANCE_BUILDER));
        }
    }
}
