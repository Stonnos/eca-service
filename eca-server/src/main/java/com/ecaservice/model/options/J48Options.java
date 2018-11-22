package com.ecaservice.model.options;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * J48 algorithm input options model.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class J48Options extends ClassifierOptions {

    /**
     * Minimum objects number per leaf
     */
    private Integer minNumObj;

    /**
     * Is binary tree?
     */
    private Boolean binarySplits;

    /**
     * Is unpruned tree?
     */
    private Boolean unpruned;

    /**
     * Folds number for tree pruning procedure
     */
    private Integer numFolds;
}
