package com.ecaservice.model.options;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import eca.ensemble.forests.DecisionTreeType;
import lombok.Data;

/**
 * Random forests options model.
 *
 * @author Roman Batygin
 */
@Data
@JsonSubTypes({
        @JsonSubTypes.Type(value = ExtraTreesOptions.class, name = ClassifierOptionsType.EXTRA_TREES),
})
public class RandomForestsOptions extends IterativeEnsembleOptions {

    /**
     * Random attributes number at each node split
     */
    private Integer numRandomAttr;

    /**
     * Min. objects per leaf
     */
    private Integer minObj;

    /**
     * Maximum tree depth
     */
    private Integer maxDepth;

    /**
     * Decision tree algorithm
     */
    private DecisionTreeType decisionTreeType;
}
