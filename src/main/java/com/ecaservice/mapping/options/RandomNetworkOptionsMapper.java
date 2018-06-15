package com.ecaservice.mapping.options;

import com.ecaservice.model.options.RandomNetworkOptions;
import eca.ensemble.RandomNetworks;
import org.mapstruct.Mapper;

/**
 * Implements Random networks input options mapping to Random networks model.
 *
 * @author Roman Batygin
 */
@Mapper
public abstract class RandomNetworkOptionsMapper extends ClassifierOptionsMapper<RandomNetworkOptions, RandomNetworks> {

    public RandomNetworkOptionsMapper() {
        super(RandomNetworkOptions.class);
    }
}
