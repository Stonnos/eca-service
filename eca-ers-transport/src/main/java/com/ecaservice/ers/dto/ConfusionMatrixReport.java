package com.ecaservice.ers.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigInteger;

import static com.ecaservice.ers.dto.Constraints.MAX_LENGTH_255;
import static com.ecaservice.ers.dto.Constraints.MIN_1;
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
     * Actual class.
     */
    @NotBlank
    @Size(min = MIN_1, max = MAX_LENGTH_255)
    @Schema(description = "Actual class", example = "Iris-setosa", requiredMode = Schema.RequiredMode.REQUIRED)
    private String actualClass;

    /**
     * Predicted class
     */
    @NotBlank
    @Size(min = MIN_1, max = MAX_LENGTH_255)
    @Schema(description = "Predicted class", example = "Iris-versicolor", requiredMode = Schema.RequiredMode.REQUIRED)
    private String predictedClass;

    /**
     * Instances number
     */
    @NotNull
    @Min(MIN_ZERO)
    @Max(Integer.MAX_VALUE)
    @Schema(description = "Instances number", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigInteger numInstances;
}
