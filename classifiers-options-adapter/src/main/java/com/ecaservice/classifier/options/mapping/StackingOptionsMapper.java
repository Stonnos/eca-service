package com.ecaservice.classifier.options.mapping;

import com.ecaservice.classifier.options.model.StackingOptions;
import eca.ensemble.StackingClassifier;
import org.mapstruct.Mapper;

/**
 * Implements stacking input options mapping to stacking model.
 *
 * @author Roman Batygin
 */
@Mapper
public abstract class StackingOptionsMapper extends ClassifierOptionsMapper<StackingOptions, StackingClassifier> {

    protected StackingOptionsMapper() {
        super(StackingOptions.class);
    }
}
