package com.ecaservice.classifier.options.mapping;

import com.ecaservice.classifier.options.model.HeterogeneousClassifierOptions;
import eca.ensemble.HeterogeneousClassifier;
import eca.ensemble.ModifiedHeterogeneousClassifier;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.core.annotation.Order;

/**
 * Implements mapping heterogeneous classifier to its options model.
 *
 * @author Roman Batygin
 */
@Order(Ordered.HEC_ORDER)
@Mapper
public abstract class HeterogeneousClassifierMapper
        extends AbstractClassifierMapper<HeterogeneousClassifier, HeterogeneousClassifierOptions> {

    protected HeterogeneousClassifierMapper() {
        super(HeterogeneousClassifier.class);
    }

    @AfterMapping
    public void postMapping(HeterogeneousClassifier classifier, @MappingTarget HeterogeneousClassifierOptions options) {
        options.setUseRandomSubspaces(classifier instanceof ModifiedHeterogeneousClassifier);
    }
}
