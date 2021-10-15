package com.ecaservice.classifier.options.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Base options model for heterogeneous ensemble.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AdaBoostOptions.class, name = ClassifierOptionsType.ADA_BOOST),
        @JsonSubTypes.Type(value = HeterogeneousClassifierOptions.class, name = ClassifierOptionsType.HEC)
})
public abstract class AbstractHeterogeneousClassifierOptions extends IterativeEnsembleOptions {

    /**
     * Min. error threshold
     */
    @Schema(description = "Classifier min. error threshold")
    private Double minError;

    /**
     * Max. error threshold
     */
    @Schema(description = "Classifier max. error threshold")
    private Double maxError;

    /**
     * Individual classifiers options
     */
    @Schema(description = "Individual classifiers options")
    private List<ClassifierOptions> classifierOptions;
}
