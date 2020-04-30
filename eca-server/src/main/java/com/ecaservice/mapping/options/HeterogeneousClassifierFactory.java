package com.ecaservice.mapping.options;

import com.ecaservice.model.options.HeterogeneousClassifierOptions;
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
        return Boolean.TRUE.equals(options.getUseRandomSubspaces()) ? new ModifiedHeterogeneousClassifier() :
                new HeterogeneousClassifier();
    }
}
