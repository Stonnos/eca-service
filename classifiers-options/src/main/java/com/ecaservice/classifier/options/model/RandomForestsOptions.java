package com.ecaservice.classifier.options.model;

import eca.ensemble.forests.DecisionTreeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Random forests options model.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RandomForestsOptions extends IterativeEnsembleOptions {

    /**
     * Random attributes number at each node split
     */
    @Schema(description = "Random attributes number at each node split")
    private Integer numRandomAttr;

    /**
     * Min. objects per leaf
     */
    @Schema(description = "Min. objects per leaf")
    private Integer minObj;

    /**
     * Maximum tree depth
     */
    @Schema(description = "Maximum tree depth")
    private Integer maxDepth;

    /**
     * Decision tree algorithm
     */
    @Schema(description = "Decision tree algorithm")
    private DecisionTreeType decisionTreeType;
}
