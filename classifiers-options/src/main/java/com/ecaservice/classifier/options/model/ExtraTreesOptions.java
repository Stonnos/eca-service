package com.ecaservice.classifier.options.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import static com.ecaservice.classifier.options.model.Constraints.VALUE_1;

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
    @Min(VALUE_1)
    @Max(Integer.MAX_VALUE)
    @Schema(description = "Number of random splits")
    private Integer numRandomSplits;

    /**
     * Is use bootstrap samples?
     */
    @Schema(description = "Use bootstrap samples flag")
    private Boolean useBootstrapSamples;
}
