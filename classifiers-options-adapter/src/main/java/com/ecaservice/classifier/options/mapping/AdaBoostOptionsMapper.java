package com.ecaservice.classifier.options.mapping;

import com.ecaservice.classifier.options.model.AdaBoostOptions;
import eca.ensemble.AdaBoostClassifier;
import org.mapstruct.Mapper;

/**
 * Implements Ada boost input options mapping to Ada boost model.
 *
 * @author Roman Batygin
 */
@Mapper
public abstract class AdaBoostOptionsMapper extends ClassifierOptionsMapper<AdaBoostOptions, AdaBoostClassifier> {

    public AdaBoostOptionsMapper() {
        super(AdaBoostOptions.class);
    }
}
