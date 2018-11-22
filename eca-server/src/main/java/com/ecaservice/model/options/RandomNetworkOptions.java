package com.ecaservice.model.options;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Random networks options model.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RandomNetworkOptions extends IterativeEnsembleOptions {

    /**
     * Min. error threshold
     */
    private Double minError;

    /**
     * Max. error threshold
     */
    private Double maxError;

    /**
     * Is use bootstrap samples?
     */
    private Boolean useBootstrapSamples;
}
