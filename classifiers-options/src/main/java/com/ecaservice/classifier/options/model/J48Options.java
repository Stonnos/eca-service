package com.ecaservice.classifier.options.model;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "Minimum objects number per leaf")
    private Integer minNumObj;

    /**
     * Is binary tree?
     */
    @Schema(description = "Binary tree flag")
    private Boolean binarySplits;

    /**
     * Is unpruned tree?
     */
    @Schema(description = "Unpruned tree flag")
    private Boolean unpruned;

    /**
     * Folds number for tree pruning procedure
     */
    @Schema(description = "Folds number for tree pruning procedure")
    private Integer numFolds;
}
