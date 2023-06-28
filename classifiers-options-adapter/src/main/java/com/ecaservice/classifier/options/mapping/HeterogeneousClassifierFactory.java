package com.ecaservice.classifier.options.mapping;

import com.ecaservice.classifier.options.model.HeterogeneousClassifierOptions;
import eca.ensemble.HeterogeneousClassifier;
import eca.ensemble.ModifiedHeterogeneousClassifier;
import org.mapstruct.ObjectFactory;
import org.springframework.stereotype.Component;

/**
 * Heterogeneous classifier factory.
 *
 * @author Roman Batygin
 */
@Component
public class HeterogeneousClassifierFactory {

    /**
     * Creates heterogeneous classifier object.
     *
     * @param options - heterogeneous classifier type
     * @return heterogeneous classifier object
     */
    @ObjectFactory
    public HeterogeneousClassifier createHeterogeneousClassifier(HeterogeneousClassifierOptions options) {
        if (Boolean.TRUE.equals(options.getUseRandomSubspaces())) {
            return new ModifiedHeterogeneousClassifier();
        }
        return new HeterogeneousClassifier();
    }
}
