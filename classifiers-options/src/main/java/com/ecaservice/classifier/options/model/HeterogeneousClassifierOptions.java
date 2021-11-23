package com.ecaservice.classifier.options.model;

import eca.ensemble.sampling.SamplingMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static com.ecaservice.classifier.options.model.Constraints.MAX_LENGTH_255;

/**
 * Heterogeneous classifier options model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Heterogeneous classifier options")
@EqualsAndHashCode(callSuper = true)
public class HeterogeneousClassifierOptions extends AbstractHeterogeneousClassifierOptions {

    /**
     * Is use weighted votes method?
     */
    @Schema(description = "Use weighted votes method")
    private Boolean useWeightedVotes;

    /**
     * Is use random classifier at each iteration?
     */
    @Schema(description = "Use random classifier at each iteration")
    private Boolean useRandomClassifier;

    /**
     * Sampling method at each iteration
     */
    @Schema(description = "Sampling method at each iteration", maxLength = MAX_LENGTH_255)
    private SamplingMethod samplingMethod;

    /**
     * Use random subspaces?
     */
    @Schema(description = "Use random subspaces")
    private Boolean useRandomSubspaces;
}
