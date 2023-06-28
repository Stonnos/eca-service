package com.ecaservice.classifier.options.mapping;

import com.ecaservice.classifier.options.model.RandomNetworkOptions;
import eca.ensemble.RandomNetworks;
import org.mapstruct.Mapper;
import org.springframework.core.annotation.Order;

/**
 * Implements Random networks input options mapping to Random networks model.
 *
 * @author Roman Batygin
 */
@Order(Ordered.RANDOM_NETWORKS_ORDER)
@Mapper
public abstract class RandomNetworkOptionsMapper extends ClassifierOptionsMapper<RandomNetworkOptions, RandomNetworks> {

    protected RandomNetworkOptionsMapper() {
        super(RandomNetworkOptions.class);
    }
}
