package com.ecaservice.ers.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(description = "Confusion matrix report")
public class ConfusionMatrixReport {

    /**
     * Actual class.
     */
    @NotBlank
    @Size(max = MAX_LENGTH_255)
    @ApiModelProperty(value = "Actual class", example = "Iris-setosa")
    private String actualClass;

    /**
     * Predicted class
     */
    @NotBlank
    @Size(max = MAX_LENGTH_255)
    @ApiModelProperty(value = "Predicted class", example = "Iris-versicolor")
    private String predictedClass;

    /**
     * Instances number
     */
    @NotNull
    @Min(MIN_ZERO)
    @ApiModelProperty(value = "Instances number", example = "10")
    private BigInteger numInstances;
}
