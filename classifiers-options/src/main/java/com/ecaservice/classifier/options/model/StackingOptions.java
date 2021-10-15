package com.ecaservice.classifier.options.model;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "Use cross - validation method for meta data building")
    private Boolean useCrossValidation;

    /**
     * Folds number for V - cross validation method
     */
    @Schema(description = "Folds number for V - cross validation method")
    private Integer numFolds;

    /**
     * Seed value for random generator
     */
    @Schema(description = "Seed value for random generator")
    private Integer seed;

    /**
     * Individual classifiers options
     */
    @Schema(description = "Individual classifiers options")
    private List<ClassifierOptions> classifierOptions;

    /**
     * Meta classifier options
     */
    @Schema(description = "Meta classifier options")
    private ClassifierOptions metaClassifierOptions;
}
