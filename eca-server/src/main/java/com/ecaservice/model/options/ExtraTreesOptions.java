package com.ecaservice.model.options;

import lombok.Data;

/**
 * Extra trees classifier options model.
 *
 * @author Roman Batygin
 */
@Data
public class ExtraTreesOptions extends RandomForestsOptions {

    /**
     * Number of random splits
     */
    private Integer numRandomSplits;

    /**
     * Is use bootstrap samples?
     */
    private Boolean useBootstrapSamples;
}
