package com.ecaservice.classifier.options.mapping;

import com.ecaservice.classifier.options.model.StackingOptions;
import eca.ensemble.StackingClassifier;
import org.mapstruct.Mapper;

/**
 * Implements mapping stacking classifier to its options model.
 *
 * @author Roman Batygin
 */
@Mapper
public abstract class StackingClassifierMapper extends AbstractClassifierMapper<StackingClassifier, StackingOptions> {

    protected StackingClassifierMapper() {
        super(StackingClassifier.class);
    }
}
