package com.ecaservice.classifier.options.mapping;

import com.ecaservice.classifier.options.model.RandomNetworkOptions;
import eca.ensemble.RandomNetworks;
import org.mapstruct.Mapper;

/**
 * Implements Random networks input options mapping to Random networks model.
 *
 * @author Roman Batygin
 */
@Mapper
public abstract class RandomNetworkOptionsMapper extends ClassifierOptionsMapper<RandomNetworkOptions, RandomNetworks> {

    protected RandomNetworkOptionsMapper() {
        super(RandomNetworkOptions.class);
    }
}
