package com.ecaservice.classifier.options.mapping;

import com.ecaservice.classifier.options.model.AdaBoostOptions;
import eca.ensemble.AdaBoostClassifier;
import org.mapstruct.Mapper;
import org.springframework.core.annotation.Order;

/**
 * Implements Ada boost input options mapping to Ada boost model.
 *
 * @author Roman Batygin
 */
@Order(Ordered.ADA_BOOST_ORDER)
@Mapper
public abstract class AdaBoostOptionsMapper extends ClassifierOptionsMapper<AdaBoostOptions, AdaBoostClassifier> {

    protected AdaBoostOptionsMapper() {
        super(AdaBoostOptions.class);
    }
}
