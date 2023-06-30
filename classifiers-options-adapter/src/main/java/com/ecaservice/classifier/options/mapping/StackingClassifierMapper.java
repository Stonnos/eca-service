package com.ecaservice.classifier.options.mapping;

import com.ecaservice.classifier.options.model.StackingOptions;
import eca.ensemble.StackingClassifier;
import org.mapstruct.Mapper;
import org.springframework.core.annotation.Order;

/**
 * Implements mapping stacking classifier to its options model.
 *
 * @author Roman Batygin
 */
@Order(Ordered.STACKING_ORDER)
@Mapper
public abstract class StackingClassifierMapper extends AbstractClassifierMapper<StackingClassifier, StackingOptions> {

    protected StackingClassifierMapper() {
        super(StackingClassifier.class);
    }
}
