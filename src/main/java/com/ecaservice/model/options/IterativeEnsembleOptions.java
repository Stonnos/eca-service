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
        @JsonSubTypes.Type(value = RandomForestsOptions.class, name = "random_forests"),
        @JsonSubTypes.Type(value = RandomNetworkOptions.class, name = "random_networks"),
        @JsonSubTypes.Type(value = AbstractHeterogeneousClassifierOptions.class, name = "abstract_hec")
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
