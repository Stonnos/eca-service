package com.ecaservice.mapping.options;

import com.ecaservice.mapping.options.AbstractClassifierMapper;
import com.ecaservice.model.options.RandomNetworkOptions;
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
