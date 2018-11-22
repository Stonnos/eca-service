package com.ecaservice.model.options;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Iterative ensemble classifier options model.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = RandomForestsOptions.class),
        @JsonSubTypes.Type(value = RandomNetworkOptions.class, name = ClassifierOptionsType.RANDOM_NETWORKS),
        @JsonSubTypes.Type(value = AbstractHeterogeneousClassifierOptions.class,
                name = ClassifierOptionsType.ABSTRACT_HEC)
})
public abstract class IterativeEnsembleOptions extends ClassifierOptions {

    /**
     * Iterations number
     */
    private Integer numIterations;

    /**
     * Threads number
     */
    private Integer numThreads;

    /**
     * Seed value for random generator
     */
    private Integer seed;
}
