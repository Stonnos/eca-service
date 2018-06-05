package com.ecaservice.mapping;

import com.ecaservice.model.options.AdaBoostOptions;
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
