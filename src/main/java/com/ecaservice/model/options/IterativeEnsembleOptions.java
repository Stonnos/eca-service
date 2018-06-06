package com.ecaservice.model.options;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

/**
 * Iterative ensemble classifier options model.
 *
 * @author Roman Batygin
 */
@Data
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes( {
        @JsonSubTypes.Type(value = RandomForestsOptions.class, name = ClassifierOptionsType.RANDOM_FORESTS),
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
