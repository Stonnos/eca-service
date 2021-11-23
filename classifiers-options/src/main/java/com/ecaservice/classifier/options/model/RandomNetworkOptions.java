package com.ecaservice.classifier.options.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;

import static com.ecaservice.classifier.options.model.Constraints.DECIMAL_VALUE_0_5_STRING;
import static com.ecaservice.classifier.options.model.Constraints.DECIMAL_VALUE_0_STRING;

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
    @DecimalMin(DECIMAL_VALUE_0_STRING)
    @DecimalMax(DECIMAL_VALUE_0_5_STRING)
    @Schema(description = "Classifier min. error threshold")
    private Double minError;

    /**
     * Max. error threshold
     */
    @DecimalMin(DECIMAL_VALUE_0_STRING)
    @DecimalMax(DECIMAL_VALUE_0_5_STRING)
    @Schema(description = "Classifier max. error threshold")
    private Double maxError;

    /**
     * Is use bootstrap samples?
     */
    @Schema(description = "Use bootstrap samples")
    private Boolean useBootstrapSamples;
}
