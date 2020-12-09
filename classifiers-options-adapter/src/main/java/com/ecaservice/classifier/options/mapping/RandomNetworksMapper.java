package com.ecaservice.classifier.options.mapping;

import com.ecaservice.classifier.options.model.RandomNetworkOptions;
import eca.ensemble.RandomNetworks;
import org.mapstruct.Mapper;

/**
 * Implements mapping Random networks classifier to its options model.
 *
 * @author Roman Batygin
 */
@Mapper
public abstract class RandomNetworksMapper extends AbstractClassifierMapper<RandomNetworks, RandomNetworkOptions> {

    protected RandomNetworksMapper() {
        super(RandomNetworks.class);
    }
}
