package com.ecaservice.classifier.options.model;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

import static com.ecaservice.classifier.options.model.Constraints.MAX_INDIVIDUAL_CLASSIFIERS;
import static com.ecaservice.classifier.options.model.Constraints.VALUE_1;
import static com.ecaservice.classifier.options.model.Constraints.VALUE_100;
import static com.ecaservice.classifier.options.model.Constraints.VALUE_2;

/**
 * Stacking classifier options model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Stacking classifier options")
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
    @Min(VALUE_2)
    @Max(VALUE_100)
    @Schema(description = "Folds number for V - cross validation method")
    private Integer numFolds;

    /**
     * Seed value for random generator
     */
    @Min(Integer.MIN_VALUE)
    @Max(Integer.MAX_VALUE)
    @Schema(description = "Seed value for random generator")
    private Integer seed;

    /**
     * Individual classifiers options
     */
    @Valid
    @NotEmpty
    @Size(min = VALUE_1, max = MAX_INDIVIDUAL_CLASSIFIERS)
    @ArraySchema(schema = @Schema(description = "Individual classifiers options"))
    private List<@NotNull ClassifierOptions> classifierOptions;

    /**
     * Meta classifier options
     */
    @Valid
    @NotNull
    @Schema(description = "Meta classifier options")
    private ClassifierOptions metaClassifierOptions;
}
