package com.ecaservice.classifier.options.model;

import eca.ensemble.forests.DecisionTreeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

import static com.ecaservice.classifier.options.model.Constraints.MAX_LENGTH_255;

/**
 * Decision tree input options model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Decision tree classifier options")
@EqualsAndHashCode(callSuper = true)
public class DecisionTreeOptions extends ClassifierOptions {

    /**
     * Decision tree algorithm
     */
    @Schema(description = "Decision tree algorithm", maxLength = MAX_LENGTH_255)
    private DecisionTreeType decisionTreeType;

    /**
     * Minimum objects number per leaf
     */
    @Schema(description = "Minimum objects number per leaf")
    private Integer minObj;

    /**
     * Maximum tree depth
     */
    @Schema(description = "Maximum tree depth")
    private Integer maxDepth;

    /**
     * Is random tree
     */
    @Schema(description = "Random tree flag")
    private Boolean randomTree;

    /**
     * Random attributes number at each split for random tree
     */
    @Schema(description = "Random attributes number at each split for random tree")
    private Integer numRandomAttr;

    /**
     * Is binary tree?
     */
    @Schema(description = "Binary tree flag")
    private Boolean useBinarySplits;

    /**
     * Is use random splits?
     */
    @Schema(description = "Use random splits flag")
    private Boolean useRandomSplits;

    /**
     * Random splits number at each node split
     */
    @Schema(description = "Random splits number at each node split")
    private Integer numRandomSplits;

    /**
     * Seed value for random generator
     */
    @Schema(description = "Seed value for random generator")
    private Integer seed;

    /**
     * Additional options map
     */
    @Schema(description = "Additional options map")
    private Map<String, String> additionalOptions;
}
