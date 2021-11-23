package com.ecaservice.classifier.options.model;

import eca.ensemble.forests.DecisionTreeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import static com.ecaservice.classifier.options.model.Constraints.MAX_LENGTH_255;
import static com.ecaservice.classifier.options.model.Constraints.ZERO_VALUE;

/**
 * Random forests options model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Random forests classifier options")
@EqualsAndHashCode(callSuper = true)
public class RandomForestsOptions extends IterativeEnsembleOptions {

    /**
     * Random attributes number at each node split
     */
    @Min(ZERO_VALUE)
    @Max(Integer.MAX_VALUE)
    @Schema(description = "Random attributes number at each node split")
    private Integer numRandomAttr;

    /**
     * Min. objects per leaf
     */
    @Min(ZERO_VALUE)
    @Max(Integer.MAX_VALUE)
    @Schema(description = "Min. objects per leaf")
    private Integer minObj;

    /**
     * Maximum tree depth
     */
    @Min(ZERO_VALUE)
    @Max(Integer.MAX_VALUE)
    @Schema(description = "Maximum tree depth")
    private Integer maxDepth;

    /**
     * Decision tree algorithm
     */
    @Schema(description = "Decision tree algorithm", maxLength = MAX_LENGTH_255)
    private DecisionTreeType decisionTreeType;
}
