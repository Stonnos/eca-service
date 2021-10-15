package com.ecaservice.classifier.options.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "Iterations number")
    private Integer numIterations;

    /**
     * Threads number
     */
    @Schema(description = "Threads number")
    private Integer numThreads;

    /**
     * Seed value for random generator
     */
    @Schema(description = "Seed value for random generator")
    private Integer seed;
}
