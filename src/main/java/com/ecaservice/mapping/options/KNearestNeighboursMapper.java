package com.ecaservice.mapping.options;

import com.ecaservice.mapping.options.AbstractClassifierMapper;
import com.ecaservice.model.options.KNearestNeighboursOptions;
import eca.metrics.KNearestNeighbours;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * Implements mapping KNN to its options model.
 *
 * @author Roman Batygin
 */
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
    @Mappings( {
            @Mapping(source = "distance.distanceType", target = "distanceType")
    })
    public abstract KNearestNeighboursOptions map(KNearestNeighbours kNearestNeighbours);
}
