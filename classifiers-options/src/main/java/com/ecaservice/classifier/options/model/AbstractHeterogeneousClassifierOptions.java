package com.ecaservice.classifier.options.model;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

import static com.ecaservice.classifier.options.model.Constraints.DECIMAL_VALUE_0_5_STRING;
import static com.ecaservice.classifier.options.model.Constraints.DECIMAL_VALUE_0_STRING;
import static com.ecaservice.classifier.options.model.Constraints.MAX_INDIVIDUAL_CLASSIFIERS;

/**
 * Base options model for heterogeneous ensemble.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractHeterogeneousClassifierOptions extends IterativeEnsembleOptions {

    /**
     * Min. error threshold
     */
    @DecimalMin(DECIMAL_VALUE_0_STRING)
    @DecimalMax(DECIMAL_VALUE_0_5_STRING)
    @Schema(description = "Classifier min. error threshold")
    private Double minError;

    /**
     * Max. error threshold
     */
    @DecimalMin(DECIMAL_VALUE_0_STRING)
    @DecimalMax(DECIMAL_VALUE_0_5_STRING)
    @Schema(description = "Classifier max. error threshold")
    private Double maxError;

    /**
     * Individual classifiers options
     */
    @Valid
    @NotEmpty
    @Size(max = MAX_INDIVIDUAL_CLASSIFIERS)
    @ArraySchema(schema = @Schema(description = "Individual classifiers options",
            ref = "#/components/schemas/ClassifierOptions"))
    private List<@NotNull ClassifierOptions> classifierOptions;
}
