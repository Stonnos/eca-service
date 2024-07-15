package com.ecaservice.ers.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigInteger;

import static com.ecaservice.ers.dto.Constraints.MIN_ZERO;

/**
 * Confusion matrix report.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Confusion matrix report")
public class ConfusionMatrixReport {

    /**
     * Actual class index
     */
    @NotNull
    @Min(MIN_ZERO)
    @Max(Integer.MAX_VALUE)
    @Schema(description = "Actual class index", example = "0", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer actualClassIndex;

    /**
     * Predicted class index
     */
    @NotNull
    @Min(MIN_ZERO)
    @Max(Integer.MAX_VALUE)
    @Schema(description = "Predicted class index", example = "0", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer predictedClassIndex;

    /**
     * Instances number
     */
    @NotNull
    @Min(MIN_ZERO)
    @Max(Integer.MAX_VALUE)
    @Schema(description = "Instances number", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigInteger numInstances;
}
