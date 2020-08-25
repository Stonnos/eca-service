package com.ecaservice.classifier.options.mapping;

import com.ecaservice.classifier.options.model.HeterogeneousClassifierOptions;
import eca.ensemble.HeterogeneousClassifier;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

/**
 * Implements HEC options mapping to HEC model.
 *
 * @author Roman Batygin
 */
@Mapper(uses = HeterogeneousClassifierFactory.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class HeterogeneousClassifierOptionsMapper
        extends ClassifierOptionsMapper<HeterogeneousClassifierOptions, HeterogeneousClassifier> {

    public HeterogeneousClassifierOptionsMapper() {
        super(HeterogeneousClassifierOptions.class);
    }
}
