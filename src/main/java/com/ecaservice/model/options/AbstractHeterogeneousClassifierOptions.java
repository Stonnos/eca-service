package com.ecaservice.model.options;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

import java.util.List;

/**
 * Base options model for heterogeneous ensemble.
 * @author Roman Batygin
 */
@Data
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes( {
        @JsonSubTypes.Type(value = AdaBoostOptions.class, name = "ada_boost"),
        @JsonSubTypes.Type(value = HeterogeneousClassifierOptions.class, name = "heterogeneous_classifier")
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
