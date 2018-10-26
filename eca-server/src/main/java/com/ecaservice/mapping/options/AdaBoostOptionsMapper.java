package com.ecaservice.mapping.options;

import com.ecaservice.model.options.AdaBoostOptions;
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
