package com.ecaservice.classifier.options.mapping;

import com.ecaservice.classifier.options.model.RandomForestsOptions;
import eca.ensemble.forests.RandomForests;
import org.mapstruct.Mapper;
import org.springframework.core.annotation.Order;

/**
 * Implements Random forests input options mapping to Random forests model.
 *
 * @author Roman Batygin
 */
@Mapper
@Order(Ordered.RANDOM_FORESTS_ORDER)
public abstract class RandomForestsOptionsMapper extends ClassifierOptionsMapper<RandomForestsOptions, RandomForests> {

    public RandomForestsOptionsMapper() {
        super(RandomForestsOptions.class);
    }
}
