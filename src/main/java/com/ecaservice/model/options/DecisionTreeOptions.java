package com.ecaservice.model.options;

import eca.ensemble.forests.DecisionTreeType;
import lombok.Data;

/**
 * Decision tree input options model.
 *
 * @author Roman Batygin
 */
@Data
public class DecisionTreeOptions extends ClassifierOptions {

    private DecisionTreeType decisionTreeType;

    private Integer minObj;

    private Integer maxDepth;

    private Integer numRandomAttrs;

    private Boolean useBinarySplits;

    private Boolean useRandomSplits;

    private Boolean randomTree;

    private Integer numRandomSplits;
}
