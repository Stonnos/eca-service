package com.ecaservice.classifier.options.mapping;

import com.ecaservice.classifier.options.model.StackingOptions;
import eca.ensemble.StackingClassifier;
import org.mapstruct.Mapper;
import org.springframework.core.annotation.Order;

/**
 * Implements stacking input options mapping to stacking model.
 *
 * @author Roman Batygin
 */
@Order(Ordered.STACKING_ORDER)
@Mapper
public abstract class StackingOptionsMapper extends ClassifierOptionsMapper<StackingOptions, StackingClassifier> {

    protected StackingOptionsMapper() {
        super(StackingOptions.class);
    }
}
