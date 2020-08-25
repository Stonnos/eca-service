package com.ecaservice.classifier.options.mapping;

import com.ecaservice.classifier.options.model.RandomForestsOptions;
import eca.ensemble.forests.RandomForests;
import org.mapstruct.Mapper;

/**
 * Implements mapping Random Forests classifier to its options model.
 *
 * @author Roman Batygin
 */
@Mapper
public abstract class RandomForestsMapper extends AbstractClassifierMapper<RandomForests, RandomForestsOptions> {

    protected RandomForestsMapper() {
        super(RandomForests.class);
    }
}
