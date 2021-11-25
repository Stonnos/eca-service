package com.ecaservice.classifier.options.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import static com.ecaservice.classifier.options.model.Constraints.VALUE_100;
import static com.ecaservice.classifier.options.model.Constraints.VALUE_2;
import static com.ecaservice.classifier.options.model.Constraints.ZERO_VALUE;

/**
 * J48 algorithm input options model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "J48 algorithm classifier options")
@EqualsAndHashCode(callSuper = true)
public class J48Options extends ClassifierOptions {

    /**
     * Minimum objects number per leaf
     */
    @Min(ZERO_VALUE)
    @Max(Integer.MAX_VALUE)
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
    @Min(VALUE_2)
    @Max(VALUE_100)
    @Schema(description = "Folds number for tree pruning procedure")
    private Integer numFolds;
}
