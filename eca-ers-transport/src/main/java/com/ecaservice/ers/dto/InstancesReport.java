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
import static com.ecaservice.ers.dto.Constraints.MIN_2;

/**
 * Training data report model.
 *
 * @author Roman Batygin
 */
@Data
@ApiModel(description = "Training data report model")
public class InstancesReport {

    /**
     * Training data structure
     */
    @NotBlank
    @ApiModelProperty(value = "Training data structure", example = "training data string")
    private String structure;

    /**
     * Relation name
     */
    @NotBlank
    @Size(max = MAX_LENGTH_255)
    @ApiModelProperty(value = "Relation name", example = "iris")
    private String relationName;

    /**
     * Instances number
     */
    @NotNull
    @Min(MIN_2)
    @ApiModelProperty(value = "Instances number", example = "150")
    private BigInteger numInstances;

    /**
     * Attributes number
     */
    @NotNull
    @Min(MIN_2)
    @ApiModelProperty(value = "Attributes number", example = "5")
    private BigInteger numAttributes;

    /**
     * Classes number
     */
    @NotNull
    @Min(MIN_2)
    @ApiModelProperty(value = "Classes number", example = "4")
    private BigInteger numClasses;

    /**
     * Class name
     */
    @NotBlank
    @Size(max = MAX_LENGTH_255)
    @ApiModelProperty(value = "Class name", example = "class")
    private String className;
}
