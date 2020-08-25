package com.ecaservice.classifier.options.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Extra trees classifier options model.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
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
