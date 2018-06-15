package com.ecaservice.mapping.options;

import com.ecaservice.model.options.RandomForestsOptions;
import eca.ensemble.forests.RandomForests;
import org.mapstruct.Mapper;

/**
 * Implements Random forests input options mapping to Random forests model.
 *
 * @author Roman Batygin
 */
@Mapper
public abstract class RandomForestsOptionsMapper extends ClassifierOptionsMapper<RandomForestsOptions, RandomForests> {

    public RandomForestsOptionsMapper() {
        super(RandomForestsOptions.class);
    }
}
