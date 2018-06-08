package com.ecaservice.model.options;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import lombok.Data;

import java.util.List;

/**
 * Base options model for heterogeneous ensemble.
 *
 * @author Roman Batygin
 */
@Data
@JsonSubTypes({
        @JsonSubTypes.Type(value = AdaBoostOptions.class, name = ClassifierOptionsType.ADA_BOOST),
        @JsonSubTypes.Type(value = HeterogeneousClassifierOptions.class, name = ClassifierOptionsType.HEC)
})
public abstract class AbstractHeterogeneousClassifierOptions extends IterativeEnsembleOptions {

    /**
     * Min. error threshold
     */
    private Double minError;

    /**
     * Max. error threshold
     */
    private Double maxError;

    /**
     * Individual classifiers options
     */
    private List<ClassifierOptions> classifierOptions;
}
