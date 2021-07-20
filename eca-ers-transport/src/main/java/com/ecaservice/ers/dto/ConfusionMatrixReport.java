package com.ecaservice.ers.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigInteger;

import static com.ecaservice.ers.dto.Constraints.MAX_LENGTH_255;
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
    @Size(max = MAX_LENGTH_255)
    @Schema(description = "Actual class", example = "Iris-setosa", required = true)
    private String actualClass;

    /**
     * Predicted class
     */
    @NotBlank
    @Size(max = MAX_LENGTH_255)
    @Schema(description = "Predicted class", example = "Iris-versicolor", required = true)
    private String predictedClass;

    /**
     * Instances number
     */
    @NotNull
    @Min(MIN_ZERO)
    @Schema(description = "Instances number", example = "10", required = true)
    private BigInteger numInstances;
}
