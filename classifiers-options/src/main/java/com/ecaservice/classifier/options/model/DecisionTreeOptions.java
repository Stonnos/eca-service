package com.ecaservice.classifier.options.model;

import eca.ensemble.forests.DecisionTreeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import static com.ecaservice.classifier.options.model.Constraints.DECIMAL_VALUE_0_STRING;
import static com.ecaservice.classifier.options.model.Constraints.DECIMAL_VALUE_1_STRING;
import static com.ecaservice.classifier.options.model.Constraints.MAX_LENGTH_255;
import static com.ecaservice.classifier.options.model.Constraints.VALUE_1;
import static com.ecaservice.classifier.options.model.Constraints.ZERO_VALUE;

/**
 * Decision tree input options model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Decision tree classifier options")
@EqualsAndHashCode(callSuper = true)
public class DecisionTreeOptions extends ClassifierOptions implements RandomizeOptions {

    /**
     * Decision tree algorithm
     */
    @NotNull
    @Schema(description = "Decision tree algorithm", maxLength = MAX_LENGTH_255)
    private DecisionTreeType decisionTreeType;

    /**
     * Minimum objects number per leaf
     */
    @Min(ZERO_VALUE)
    @Max(Integer.MAX_VALUE)
    @Schema(description = "Minimum objects number per leaf")
    private Integer minObj;

    /**
     * Maximum tree depth
     */
    @Min(ZERO_VALUE)
    @Max(Integer.MAX_VALUE)
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
    @Min(ZERO_VALUE)
    @Max(Integer.MAX_VALUE)
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
    @Min(VALUE_1)
    @Max(Integer.MAX_VALUE)
    private Integer numRandomSplits;

    /**
     * Seed value for random generator
     */
    @Min(Integer.MIN_VALUE)
    @Max(Integer.MAX_VALUE)
    @Schema(description = "Seed value for random generator")
    private Integer seed;

    /**
     * Alpha value for chi square test
     */
    @DecimalMin(value = DECIMAL_VALUE_0_STRING, inclusive = false)
    @DecimalMax(value = DECIMAL_VALUE_1_STRING, inclusive = false)
    @Schema(description = "Alpha value for chi square test")
    private Double alpha;
}
