package com.ecaservice.classifier.options.mapping;

import com.ecaservice.classifier.options.model.KNearestNeighboursOptions;
import eca.metrics.KNearestNeighbours;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.core.annotation.Order;

/**
 * Implements mapping KNN to its options model.
 *
 * @author Roman Batygin
 */
@Order(Ordered.KNN_ORDER)
@Mapper
public abstract class KNearestNeighboursMapper
        extends AbstractClassifierMapper<KNearestNeighbours, KNearestNeighboursOptions> {

    protected KNearestNeighboursMapper() {
        super(KNearestNeighbours.class);
    }

    /**
     * Maps KNN to its options model
     *
     * @param kNearestNeighbours - KNN object
     * @return KNN options model
     */
    @Mapping(source = "distance.distanceType", target = "distanceType")
    public abstract KNearestNeighboursOptions map(KNearestNeighbours kNearestNeighbours);
}
