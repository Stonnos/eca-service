package com.ecaservice.classifier.options.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Random networks options model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Random networks classifier options")
@EqualsAndHashCode(callSuper = true)
public class RandomNetworkOptions extends IterativeEnsembleOptions {

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
     * Is use bootstrap samples?
     */
    @Schema(description = "Use bootstrap samples")
    private Boolean useBootstrapSamples;
}
