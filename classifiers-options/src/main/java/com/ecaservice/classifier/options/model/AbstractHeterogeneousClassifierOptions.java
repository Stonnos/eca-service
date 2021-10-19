package com.ecaservice.classifier.options.model;

import io.swagger.v3.oas.annotations.media.ArraySchema;
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
    @ArraySchema(schema = @Schema(description = "Individual classifiers options",
            ref = "#/components/schemas/ClassifierOptions"))
    private List<ClassifierOptions> classifierOptions;
}
