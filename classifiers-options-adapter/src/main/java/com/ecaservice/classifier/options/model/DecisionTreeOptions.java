package com.ecaservice.classifier.options.model;

import eca.ensemble.forests.DecisionTreeType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * Decision tree input options model.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DecisionTreeOptions extends ClassifierOptions {

    /**
     * Decision tree algorithm
     */
    private DecisionTreeType decisionTreeType;

    /**
     * Minimum objects number per leaf
     */
    private Integer minObj;

    /**
     * Maximum tree depth
     */
    private Integer maxDepth;

    /**
     * Is random tree
     */
    private Boolean randomTree;

    /**
     * Random attributes number at each split for random tree
     */
    private Integer numRandomAttr;

    /**
     * Is binary tree?
     */
    private Boolean useBinarySplits;

    /**
     * Is use random splits?
     */
    private Boolean useRandomSplits;

    /**
     * Random splits number at each node split
     */
    private Integer numRandomSplits;

    /**
     * Seed value for random generator
     */
    private Integer seed;

    /**
     * Additional options map
     */
    private Map<String, String> additionalOptions;
}
