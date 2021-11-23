package com.ecaservice.classifier.options.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Extra trees classifier options model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Extra trees classifier options")
@EqualsAndHashCode(callSuper = true)
public class ExtraTreesOptions extends RandomForestsOptions {

    /**
     * Number of random splits
     */
    @Schema(description = "Number of random splits")
    private Integer numRandomSplits;

    /**
     * Is use bootstrap samples?
     */
    @Schema(description = "Use bootstrap samples flag")
    private Boolean useBootstrapSamples;
}
