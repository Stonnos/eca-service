package com.ecaservice.classifier.options.mapping;

import com.ecaservice.classifier.options.model.AdaBoostOptions;
import eca.ensemble.AdaBoostClassifier;
import org.mapstruct.Mapper;

/**
 * Implements mapping AdaBoost classifier to AdaBoost options model.
 *
 * @author Roman Batygin
 */
@Mapper
public abstract class AdaBoostMapper extends AbstractClassifierMapper<AdaBoostClassifier, AdaBoostOptions> {

    protected AdaBoostMapper() {
        super(AdaBoostClassifier.class);
    }
}
