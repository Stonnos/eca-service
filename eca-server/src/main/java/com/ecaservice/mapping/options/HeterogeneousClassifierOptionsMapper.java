package com.ecaservice.mapping.options;

import com.ecaservice.model.options.HeterogeneousClassifierOptions;
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
