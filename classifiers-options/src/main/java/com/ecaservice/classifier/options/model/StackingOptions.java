package com.ecaservice.classifier.options.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Stacking classifier options model.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StackingOptions extends ClassifierOptions {

    /**
     * Is use cross - validation method for meta data building?
     */
    private Boolean useCrossValidation;

    /**
     * Folds number for V - cross validation method
     */
    private Integer numFolds;

    /**
     * Seed value for random generator
     */
    private Integer seed;

    /**
     * Individual classifiers options
     */
    private List<ClassifierOptions> classifierOptions;

    /**
     * Meta classifier options
     */
    private ClassifierOptions metaClassifierOptions;
}
