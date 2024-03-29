package com.ecaservice.classifier.options.mapping;

import com.ecaservice.classifier.options.model.HeterogeneousClassifierOptions;
import eca.ensemble.HeterogeneousClassifier;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.springframework.core.annotation.Order;

/**
 * Implements HEC options mapping to HEC model.
 *
 * @author Roman Batygin
 */
@Order(Ordered.HEC_ORDER)
@Mapper(uses = HeterogeneousClassifierFactory.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class HeterogeneousClassifierOptionsMapper
        extends ClassifierOptionsMapper<HeterogeneousClassifierOptions, HeterogeneousClassifier> {

    protected HeterogeneousClassifierOptionsMapper() {
        super(HeterogeneousClassifierOptions.class);
    }
}
